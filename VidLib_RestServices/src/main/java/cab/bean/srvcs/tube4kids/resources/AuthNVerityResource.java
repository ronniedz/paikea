package cab.bean.srvcs.tube4kids.resources;

import static java.util.Collections.singletonMap;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import org.apache.commons.beanutils.BeanUtils;
import org.jose4j.json.JsonUtil;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.GoogleAPIClientConfiguration;
import cab.bean.srvcs.tube4kids.JWTConfiguration;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.Role.Names;
import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.RoleDAO;
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
import com.google.common.collect.ImmutableMap;

import     javax.ws.rs.core.HttpHeaders;


/**
 * Authentication Resource. Offers 2 means for logging out: /logout as well as /revoke.
 * 'revoke' provides a means to POST the id_token and maybe use by an admin to revoke  another's token 
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/auth")
public class AuthNVerityResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthNVerityResource.class);

    private static final int preOffset = 1; // minute

    
    // TODO Move to Application layer or to AppConfiguration. Should be shared by all.
    
    private final JsonFactory jsonFactory = new JacksonFactory();
//    private String clientId = null;
//    private String jwtTokenSecret = null;

    private TokenDAO tokenDAO;
    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private GoogleAPIClientConfiguration googleAPIConf;

    private JWTConfiguration jwtConf;

    public AuthNVerityResource(
	    TokenDAO tokenDAO, UserDAO userDAO, RoleDAO roleDAO,
	    GoogleAPIClientConfiguration googleAPIConf,
	    JWTConfiguration jwtConf) {

	this.googleAPIConf = googleAPIConf;
	this.jwtConf = jwtConf;
	
	this.tokenDAO = tokenDAO;
	this.userDAO = userDAO;
	this.roleDAO = roleDAO;

	LOGGER.debug("started. loaded \n{}\n{}\n{}\n{}" , tokenDAO, userDAO, googleAPIConf, jwtConf);

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
	URI redirUri = null;
	
	try {
	    final IdTokenVerity.UserValue userValues = itv.verifyToken(idTokenString);
	    
	    if (userValues != null) {
		
		final String subject = userValues.getSubject();

		Token beanToken = null;
		User user =  null;
		
		LOGGER.debug("subject in: '{}'", subject);
		
//		Optional<Token> t = tokenDAO.findBySubjectAndIssuer(subject, userValues.getIssuer());
		Optional<Token> t = tokenDAO.findBySubject(subject);

		if ( t.isPresent() )  {
		    
		    beanToken = t.get();
		    user = beanToken.getUser();
		    
		    dat.setStatus(Status.OK);

		    LOGGER.debug("t.isPresent beanToken:\n'{}'", beanToken);
		} else {

		    user = createUser(userValues);
		    beanToken = new Token(user, subject);
		    beanToken.setAudience(jwtConf.getAudienceId());
		    
		    // Original issuer now saved as IdProvider
		    beanToken.setIdp(userValues.getIssuer());
		    // Make this server the issuer of this bean
		    beanToken.setIssuer(request.getServerName());

		    dat.setStatus(Status.CREATED);

		    LOGGER.debug("t.notPresent beanToken:\n'{}'", beanToken);
		}
		// False when revoked.
		beanToken.setActive(true);
		// Remove prior value
		userValues.removeIssuer();
		updateUser(user, userValues, beanToken);
		
		int ttl_mins =  jwtConf.getTokenExpiration();
		
		jwt = makeJwt(userValues, beanToken, ttl_mins);
		redirUri = getRedirect(user);
//		dat.setLocation(redirUri);

		dat.setEntity(
	            ImmutableMap.of(
	            	"access_token", jwt,
	                	"expires_in", (ttl_mins - preOffset) * 60,
	                	"token_type", "Bearer"
	            )
		);
	    }
	} catch (GeneralSecurityException | IOException e) {
	    e.printStackTrace();
	    dat.setSuccess(false).setErrorMessage(e.getMessage()).setEntity(e.getCause());
	}
	ResponseBuilder rb = doPOST(dat);
	if (jwt != null) {

	    Calendar c = Calendar.getInstance();
	    // Expire cookie before token
	    c.add(Calendar.MINUTE, jwtConf.getTokenExpiration() - preOffset);
	    final Date expiry = c.getTime();
	    final int maxAge = jwtConf.getMaxAge();
	    rb.cookie(genCookie(jwt, jwtConf, request, maxAge, expiry));
	}
	return rb.build();
    }

    @Path("logout")
    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {

	Optional<String> idTokenStringOpt = getTokenFromCookieOrHeader(request);

	LOGGER.debug("deleting token {}", idTokenStringOpt.get());
	ResponseData dat = (new ResponseData()).setSuccess(false);

	if (idTokenStringOpt.isPresent()) {
	    String idTokenString = idTokenStringOpt.get();

	    IdTokenVerity itv = new IdTokenVerity(googleAPIConf.getClientId(), jsonFactory);

	    GoogleIdToken googleToken;
	    try {
		googleToken = itv.parse(idTokenString);


		final Payload payload = googleToken.getPayload();
		final String subject = payload.getSubject();

		Token beanToken = null;

		// Optional<Token> t = tokenDAO.findBySubjectAndIssuer(subject,
		Optional<Token> t = tokenDAO.findBySubject(subject);

		if (t.isPresent()) {

		    beanToken = t.get();

		    // Invalidate the token
		    beanToken.setActive(false);
		    tokenDAO.create(beanToken);
		    
		    dat.setStatus(Status.OK);
		    LOGGER.debug("t.isPresent beanToken:\n'{}'", beanToken);
		}
	    } catch (IOException e) {
		throw new javax.ws.rs.BadRequestException(e.getCause());
	    }
	}

	Calendar c = Calendar.getInstance();
	c.add(Calendar.MINUTE, 1);

	// Zero, to retire cookie
	final int maxAge = 0;

	return doGET(dat).cookie(
		genCookie("", jwtConf, request, maxAge, c.getTime())
	).build();
    }


    @Path("revoke")
    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Role.Names.MEMBER_ROLE, Role.Names.ADMIN_ROLE}) 
    public Response revoke(@FormParam("id_token") String idTokenString, @Context HttpServletRequest request) {
	
	LOGGER.debug("deleting token {}" , idTokenString);
	ResponseData dat = (new ResponseData()).setSuccess(false);
	
	    IdTokenVerity itv = new IdTokenVerity(googleAPIConf.getClientId(), jsonFactory);
	    
	    GoogleIdToken googleToken;
	    try {
		googleToken = itv.parse(idTokenString);
		
		final Payload payload = googleToken.getPayload();
		final String subject = payload.getSubject();
		
		Token beanToken = null;
		User user =  null;
		
		LOGGER.debug("subject in: '{}'", subject);
		
		Optional<Token> t = tokenDAO.findBySubject(subject);
		
		if ( t.isPresent() )  {
		    
		    beanToken = t.get();
		    
		    beanToken.setActive(false);
		    tokenDAO.create(beanToken);
		    dat.setStatus(Status.OK);
		    LOGGER.debug("t.isPresent beanToken:\n'{}'", beanToken);
		} 
	    } catch (IOException e) {
		e.printStackTrace();
		throw  new javax.ws.rs.BadRequestException(e.getCause());
	    }
	    
	
	Calendar c = Calendar.getInstance();
	c.add(Calendar.MINUTE, 1);
	// Zero, to retire cookie
	final int maxAge = 0;
	// dat.setSuccess(true);
	return  doPOST(dat).cookie(
		genCookie("", jwtConf, request, maxAge,  c.getTime())
	).build();
    }
    

    private URI getRedirect(User user) {
	URI uri = null;
	
	try {
	    uri = new URI("/api/user/" + user.getId());
	} catch (URISyntaxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	return uri;
	
    }


    private Optional<String> getTokenFromCookieOrHeader(HttpServletRequest request) {
	Enumeration<String> re = request.getHeaders(AUTHORIZATION);
	if ( re != null && re.hasMoreElements()) {
	    final Optional<String> headerToken = getTokenFromHeader(re.nextElement());
    
            if (headerToken.isPresent()) {
                return headerToken;
            }
	}
        final Optional<String> cookieToken = getTokenFromCookie(request);
        return cookieToken.isPresent() ? cookieToken : Optional.empty();
    }


    private Optional<String> getTokenFromHeader( final String header) {
        if (header != null) {
            int space = header.indexOf(' ');
            if (space > 0) {
                final String method = header.substring(0, space);
                if (jwtConf.getAuthHeaderPrefix().equalsIgnoreCase(method)) {
                    final String rawToken = header.substring(space + 1);
                    return Optional.of(rawToken);
                }
            }
        }

        return Optional.empty();
    }


    private Optional<String> getTokenFromCookie(HttpServletRequest request) {
	for( Cookie tokenCookie: request.getCookies()) {
            if (tokenCookie.getName().equals(jwtConf.getCookieName())) {
                return Optional.of(tokenCookie.getValue());
            }
        }
        return Optional.empty();
    }


    private NewCookie genCookie(String value, JWTConfiguration conf, HttpServletRequest request, int maxAge, Date expiry) {

	final String name = conf.getCookieName();
        final String path = request.getServletContext().getContextPath() + "/";
        final String domain = request.getServerName();
        final String comment = "Access Token";
	final boolean secure = conf.isSecure();
	boolean httpOnly = conf.isHttpOnly();
	

	LOGGER.debug("name {},  path: {} , domain {} ,  maxAge: {} , expiry: {} , secure: {} , httpOnly: {} ",
		name, path, domain, maxAge, expiry, secure, httpOnly);

	return new
	  NewCookie(name, value, path, domain, NewCookie.DEFAULT_VERSION, comment,
		  maxAge, expiry, secure, httpOnly);

/*
        NewCookie(String name, String value, String path, String domain, int version, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly)

        Create a new instance.

        Parameters:
        name the name of the cookie
        value the value of the cookie
        path the URI path for which the cookie is valid
        domain the host domain for which the cookie is valid
        version the version of the specification to which the cookie complies
        comment the comment
        maxAge the maximum age of the cookie in seconds
        expiry the cookie expiry date.
        secure specifies whether the cookie will only be sent over a secure connection
        httpOnly if true make the cookie HTTP only, i.e. only visible as part of an HTTP request.
*/
    }


    private void updateUser(User user, Map<String, Object> userValues, Token beanToken) {

	try {
	    BeanUtils.copyProperties(user, userValues);
	} catch (IllegalAccessException | InvocationTargetException e) {
	    e.printStackTrace();
	}

	beanToken.setUser(userDAO.create(user));
	
	tokenDAO.create(beanToken);
	userValues.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

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
    
	for(String rname : Names.MEMBER) {
	    u.addRole(roleDAO.findByName(rname).get());
	}
	return u;
    }


    @SuppressWarnings("unchecked")
    private String makeJwt(Map<String, Object> m,  Token beanToken, float token_ttl) {

	// Create the Claims, which will be the content of the JWT
        final JwtClaims claims = new JwtClaims();
        claims.setIssuer(beanToken.getIssuer());  // who creates the token and signs it
        claims.setSubject(beanToken.getSubject());
        
        claims.setAudience(beanToken.getAudience()); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(token_ttl); // time when the token will expire
//        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)

        m.entrySet().forEach(x -> { 
            if ( x.getValue() instanceof java.util.Collection) {
                claims.setStringListClaim(x.getKey(), (List<String>) x.getValue()); // multi-valued claims work too and will end up as a JSON array
            } else {
        		claims.setClaim(x.getKey(), x.getValue()); // additional claims/attributes about the subject can be added
            }
        });

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
