package cab.bean.srvcs.tube4kids.resources;

import static java.util.Collections.singletonMap;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;
import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.beanutils.BeanUtils;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Throwables;

import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;


@Path("/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class GoogleAuthNResource extends BaseResource {


    private final JsonFactory jsonFactory = new JacksonFactory();
    private String CLIENT_ID = "16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com";
    private String jwtTokenSecret = "";

    private TokenDAO tokenDAO;
    private UserDAO userDAO;


    public GoogleAuthNResource(TokenDAO tokenDAO, UserDAO userDAO, String jwtTokenSecret, String CLIENT_ID ) {
	this.jwtTokenSecret = jwtTokenSecret;
	this.CLIENT_ID = CLIENT_ID;
	this.tokenDAO = tokenDAO;
	this.userDAO = userDAO;
    }

    @POST
    @UnitOfWork
    @Path("callback")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response callback(@FormParam("id_token") String idTokenString) {

	ResponseData dat = new ResponseData();
	
	GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder( new NetHttpTransport(), jsonFactory)
	  .setAudience(Collections.singletonList(CLIENT_ID)).build();

	try {
//		    GoogleIdToken idToken = GoogleIdToken.parse( jsonFactory, idTokenString );
	    GoogleIdToken idToken = verifier.verify(idTokenString);
	    if (idToken != null) {
		Map userValues = extractValues(idToken);
		
		Map jwt = makeJwt(idToken, userValues);
		dat.setSuccess(jwt != null).setEntity(jwt);
		
		Optional<Token> t = tokenDAO.findUserWithToken(idToken.getPayload().getSubject());
		if (! t.isPresent() ) {
		    User user = createUser(userValues);
		    Token beanToken = new Token(user, idToken.getPayload().getSubject(), idToken.getPayload().getIssuer());
		    tokenDAO.create(beanToken);
		}
	    }
	} catch (GeneralSecurityException | IOException e) {
	    e.printStackTrace();
	    dat.setSuccess(false).setErrorMessage(e.getMessage()).setEntity(e.getCause());
	}
	return doPOST(dat).build();

    }
private User createUser(Map userValues) {
    User u = new User();

    try {
	BeanUtils.copyProperties(u, userValues);
    } catch (IllegalAccessException | InvocationTargetException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
    }
    u.setEmailVerified(Boolean.TRUE);
    u.setActivated(Boolean.TRUE);
    u.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));

   return userDAO.create(u);
    }

private Map<String, Object> extractValues(GoogleIdToken idToken) {
    Map<String, Object> m = new HashMap();
    m.put("email", idToken.getPayload().getEmail());
    m.put("email_verified", idToken.getPayload().getEmailVerified());
    m.put("firstname", idToken.getPayload().getUnknownKeys().get("given_name"));
    m.put("firstname", idToken.getPayload().getUnknownKeys().get("given_name")); // additional claims/attributes about the subject can be added
    m.put("lastname", idToken.getPayload().getUnknownKeys().get("family_name")); // additional claims/attributes about the subject can be added
    m.put("picture",idToken.getPayload().getUnknownKeys().get("picture")); // additional claims/attributes about the subject can be added
    m.put("locale",idToken.getPayload().getUnknownKeys().get("locale")); // additional claims/attributes about the subject can be added
    return m;
}
    private Map<String, String> makeJwt(GoogleIdToken idToken, Map<String, Object> m) {
//	final  Map<String, Object> m = new Hashtable<String, Object>();
//        
//        idToken.getPayload().entrySet().forEach(x -> { m.put(x.getKey(), x.getValue()); });
//        System.err.println(m); 
        // Create the Claims, which will be the content of the JWT
        final JwtClaims claims = new JwtClaims();
        claims.setIssuer(idToken.getPayload().getIssuer());  // who creates the token and signs it
        claims.setSubject(idToken.getPayload().getSubject());
        
        claims.setAudience(CLIENT_ID); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)

        m.entrySet().forEach(x -> { 
            claims.setClaim(x.getKey(), x.getValue()); // additional claims/attributes about the subject can be added
        });

        List<String> groups = Arrays.asList("ADMIN");
        claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        final JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
//        jws.setKey(rsaJsonWebKey.getPrivateKey());
        try {
	    jws.setKey(new HmacKey(jwtTokenSecret.getBytes("UTF-8")));
	} catch (UnsupportedEncodingException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
//        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setKeyIdHeaderValue("beancab");

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
//        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setAlgorithmHeaderValue(HMAC_SHA256);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        try {
            return singletonMap("token",  jws.getCompactSerialization());
        }
        catch (JoseException e) { throw Throwables.propagate(e); }
    }
}
