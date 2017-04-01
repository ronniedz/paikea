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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.GoogleAPIClientConfiguration;
import cab.bean.srvcs.tube4kids.JWTConfiguration;
import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.resources.utils.IdTokenVerity;

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
//    private String clientId = null;
//    private String jwtTokenSecret = null;

    private TokenDAO tokenDAO;
    private UserDAO userDAO;
    private GoogleAPIClientConfiguration googleAPIConf;

    private JWTConfiguration jwtConf;

    public AuthNVerityResource(
	    TokenDAO tokenDAO, UserDAO userDAO,
	    GoogleAPIClientConfiguration googleAPIConf,
	    JWTConfiguration jwtConf) {

	this.googleAPIConf = googleAPIConf;
	this.jwtConf = jwtConf;
	
	this.tokenDAO = tokenDAO;
	this.userDAO = userDAO;
    }

    @POST
    @UnitOfWork
    @Path("gcallback")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response googleCallback(@FormParam("id_token") String idTokenString, @Context HttpServletRequest request) {

	LOGGER.debug("got idTokenString");

	ResponseData dat = (new ResponseData())
		.setSuccess(false)
		.setStatus(Status.UNAUTHORIZED);
	
	IdTokenVerity itv = new IdTokenVerity(googleAPIConf.getClientId(), jsonFactory);

	String jwt = null;
	try {
	    final IdTokenVerity.UserValue userValues = itv.verifyToken(idTokenString);
	    
	    if (userValues != null) {
		
		final String subject = userValues.getSubject();

		Token beanToken = null;
		User user =  null;
		
		LOGGER.debug("subject in: '{}'", subject);
		
		Optional<Token> t = tokenDAO.findBySubject(subject);

		if ( t.isPresent() )  {

		    beanToken = t.get();
		    user = beanToken.getUser();
		    
		    dat.setStatus(Status.OK);

		    LOGGER.debug("t.isPresent beanToken:\n'{}'", beanToken);
		} else {

		    user = createUser(userValues);
		    beanToken = new Token(user, subject, userValues.getIssuer());
		    beanToken.setAudience(jwtConf.getAudienceId());

		    dat.setStatus(Status.CREATED);

		    LOGGER.debug("t.notPresent beanToken:\n'{}'", beanToken);
		}

		updateUser(user, userValues, beanToken);
		
		jwt = makeJwt(userValues, beanToken);
		
		dat.setEntity(singletonMap("token", jwt));
	    }
	} catch (GeneralSecurityException | IOException e) {
	    e.printStackTrace();
	    dat.setSuccess(false).setErrorMessage(e.getMessage()).setEntity(e.getCause());
	}
	ResponseBuilder rb = doPOST(dat);
	if (jwt != null) {
	    rb.cookie(new NewCookie(jwtConf.getCookieName(), jwt));
	}
	return rb.build();
    }

    private void updateUser(User user, Map<String, Object> userValues, Token beanToken) {

	try {
	    BeanUtils.copyProperties(user, userValues);
	} catch (IllegalAccessException | InvocationTargetException e) {
	    e.printStackTrace();
	}

	beanToken.setUser(userDAO.create(user));
	tokenDAO.create(beanToken);
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

	return u;
    }

    private String makeJwt(Map<String, Object> m,  Token beanToken) {

        // Create the Claims, which will be the content of the JWT
        final JwtClaims claims = new JwtClaims();
        claims.setIssuer(beanToken.getIssuer());  // who creates the token and signs it
        claims.setSubject(beanToken.getSubject());
        
        claims.setAudience(beanToken.getAudience()); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
//        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)

        m.entrySet().forEach(x -> { 
            claims.setClaim(x.getKey(), x.getValue()); // additional claims/attributes about the subject can be added
        });

        // TODO DETERMINE PROPER ROLES
        claims.setStringListClaim("groups", Arrays.asList("editor", "user", "ADMIN")); // multi-valued claims work too and will end up as a JSON array

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
//        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKey(jwtConf.getVerificationKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
//        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setKeyIdHeaderValue(jwtConf.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
//        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        jws.setAlgorithmHeaderValue(jwtConf.getSignatureAlgorithm());

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        try {
            return  jws.getCompactSerialization();
        }
        catch (JoseException e) { throw Throwables.propagate(e); }
    }
}
