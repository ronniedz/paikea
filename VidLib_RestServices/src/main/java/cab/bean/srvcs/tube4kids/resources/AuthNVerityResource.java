package cab.bean.srvcs.tube4kids.resources;

import static java.util.Collections.singletonMap;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;
import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Throwables;


@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthNVerityResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthNVerityResource.class);

    private final JsonFactory jsonFactory = new JacksonFactory();
    private String clientId = null;
    private String jwtTokenSecret = null;

    private TokenDAO tokenDAO;
    private UserDAO userDAO;

    public AuthNVerityResource(TokenDAO tokenDAO, UserDAO userDAO, String jwtTokenSecret, String clientId ) {
	this.jwtTokenSecret = jwtTokenSecret;
	this.clientId = clientId;
	this.tokenDAO = tokenDAO;
	this.userDAO = userDAO;
    }

    @POST
    @UnitOfWork
    @Path("gcallback")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response googleCallback(@FormParam("id_token") String idTokenString) {

	ResponseData dat = new ResponseData();
	    LOGGER.debug("got idTokenString: " + idTokenString);

	GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder( new NetHttpTransport(), jsonFactory)
	  .setAudience(Collections.singletonList(clientId)).build();

	try {
//	    GoogleIdToken idToken = GoogleIdToken.parse( jsonFactory, idTokenString );
	    GoogleIdToken idToken = verifier.verify(idTokenString);
	    if (idToken != null) {
		
		Optional<Token> t = tokenDAO.findBySubject(idToken.getPayload().getSubject());
		if (! t.isPresent() ) {
		    Map<String, Object> userValues = extractValues(idToken);
		    
		    User user = createUser(userValues);
		    
		    Token beanToken = new Token(user, idToken.getPayload().getSubject(), idToken.getPayload().getIssuer());
		    tokenDAO.create(beanToken);
		    Map jwt = makeJwt(idToken, userValues);
		    LOGGER.debug("made: " + jwt);
		    dat.setSuccess(true).setEntity(jwt);
		}
	    }
	} catch (GeneralSecurityException | IOException e) {
	    e.printStackTrace();
	    dat.setSuccess(false).setErrorMessage(e.getMessage()).setEntity(e.getCause());
	}
	return doPOST(dat).build();
    }

    private User createUser(Map<String, Object> userValues) {
	User u = new User();

	try {
	    BeanUtils.copyProperties(u, userValues);
	} catch (IllegalAccessException | InvocationTargetException e) {
	    e.printStackTrace();
	}
	// TODO figure out issue with this boolean propagation
//	u.setEmailVerified((Boolean) userValues.getOrDefault("email_verified", Boolean.TRUE));
	u.setEmailVerified(Boolean.TRUE);
	u.setActivated(Boolean.TRUE);
	u.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));

	return userDAO.create(u);
    }

    private Map<String, Object> extractValues(GoogleIdToken idToken) {
	Map<String, Object> m = new HashMap<String, Object>();
	Payload payload = idToken.getPayload();
	m.put("email", payload.getEmail());
	m.put("email_verified", payload.getEmailVerified());
	m.put("firstname", payload.getUnknownKeys().get("given_name"));
	m.put("lastname", payload.getUnknownKeys().get("family_name"));
	m.put("picture", payload.getUnknownKeys().get("picture"));
	m.put("locale", payload.getUnknownKeys().get("locale"));
	return m;
    }

    private Map<String, String> makeJwt(GoogleIdToken idToken, Map<String, Object> m) {

        // Create the Claims, which will be the content of the JWT
        final JwtClaims claims = new JwtClaims();
        claims.setIssuer(idToken.getPayload().getIssuer());  // who creates the token and signs it
        claims.setSubject(idToken.getPayload().getSubject());
        
        claims.setAudience(clientId); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)

        m.entrySet().forEach(x -> { 
            claims.setClaim(x.getKey(), x.getValue()); // additional claims/attributes about the subject can be added
        });

        // TODO DETERMINE PROPER ROLES
        List<String> groups = Arrays.asList("editor", "user", "ADMIN");
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
