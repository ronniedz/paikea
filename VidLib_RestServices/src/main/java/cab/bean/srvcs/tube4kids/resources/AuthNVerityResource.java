package cab.bean.srvcs.tube4kids.resources;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import io.dropwizard.hibernate.UnitOfWork;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.GoogleAPIClientConfiguration;
import cab.bean.srvcs.tube4kids.JWTConfiguration;
import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.auth.TokenService;
import cab.bean.srvcs.tube4kids.auth.utils.SubjectData;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.RoleDAO;
import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.exception.ConfigurationException;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;


/**
 * Authentication Resource. Offers 2 means for logging out: /logout as well as /revoke.
 * 'revoke' provides a means to POST the id_token and maybe use by an admin to revoke  another's token 
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/auth")
public class AuthNVerityResource extends BaseResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthNVerityResource.class);
    private static final int preOffset = 1; // minute
    
    private final TokenDAO tokenDAO;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final GoogleAPIClientConfiguration googleAPIConf;
    private final JWTConfiguration jwtConf;

    private final TokenService tokenService;
    
    public AuthNVerityResource( TokenDAO tokenDAO, UserDAO userDAO, RoleDAO roleDAO, GoogleAPIClientConfiguration googleAPIConf, JWTConfiguration jwtConf)
    {
	this.googleAPIConf = googleAPIConf;
	this.jwtConf = jwtConf;
	this.tokenService = new TokenService(jwtConf);
	this.tokenDAO = tokenDAO;
	this.userDAO = userDAO;
	this.roleDAO = roleDAO;
    }

    @POST
    @UnitOfWork
    @Path("gcallback")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response googleCallback(@FormParam("id_token") String idTokenString,@Context HttpServletRequest request) {

	final ResponseData dat = (new ResponseData()).setSuccess(false).setStatus(Status.UNAUTHORIZED);

	String jwt = null;
	
	try {
	    final SubjectData userValues = new TokenService(googleAPIConf).verifyToData(idTokenString);;
	    final String subject = userValues.getSubject();

	    if (userValues != null) {

		final Token beanToken = tokenDAO.locateSubject(userValues)
			.orElse( new Token(createUser(userValues), subject)
                		    .setAudience(jwtConf.getAudience()[0]) // Original issuer now saved as IdProvider
                		    .setIdp(userValues.getIssuer())		// Make this server the issuer of the token
                		    .setIssuer(jwtConf.getIssuer()[0]));
		
		// False when revoked.
		beanToken.setActive(true).setSubject(subject);
		// Remove prior value
		userValues.removeIssuer();
		
		updateUser(userValues, beanToken);

		jwt = tokenService.generate(userValues);

		dat.setStatus(Status.OK)
		   .setLocation(getRedirect(beanToken.getUser()))
		   .setEntity(
			   ImmutableMap.of("access_token", jwt,"expires_in", (jwtConf.getTokenExpiration() - preOffset) * 60, "token_type", "Bearer")
		   );
	    }
	} catch (InvalidJwtException | JoseException | IOException | ConfigurationException e) {
	    e.printStackTrace();
	    dat.setSuccess(false).setErrorMessage(e.getMessage())
		    .setEntity(e.getCause());
		throw new ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED());

	}
	ResponseBuilder rb = doPOST(dat);
	if (jwt != null) {

	    Calendar c = Calendar.getInstance();
	    // Expire cookie before token
	    c.add(Calendar.MINUTE, jwtConf.getTokenExpiration() - preOffset);
	    final Date expiry = c.getTime();
	    final int maxAge = jwtConf.getCookieMaxAge();
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

	    try {
		SubjectData userValues =  TokenService.parseToData(idTokenString);
		final String subject = userValues.getSubject();

		if (deactivateToken(subject)) {
		    dat.setStatus(Status.OK);
		}
	    } catch (InvalidJwtException e) {
		throw new javax.ws.rs.BadRequestException(e.getCause());
	    }
	}

	Calendar c = Calendar.getInstance();
	c.add(Calendar.MINUTE, 1);

	// Zero, to retire cookie
	final int maxAge = 0;
	return
		doGET(dat).cookie(genCookie("", jwtConf, request, maxAge, c.getTime()))
		.build();
    }

    private boolean deactivateToken(String subject) {
	// Optional<Token> t = tokenDAO.findBySubjectAndIssuer(subject,
	Optional<Token> t = tokenDAO.findBySubject(subject);
	if (t.isPresent()) {
	    Token beanToken = t.get();
	    // Invalidate the token
	    beanToken.setActive(false);
	    tokenDAO.create(beanToken);
	    return true;
	}
	return false;
    }

    @Path("revoke")
    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({RoleNames.MEMBER_ROLE, RoleNames.ADMIN_ROLE}) 
    public Response revoke(@FormParam("id_token") String idTokenString, @Context HttpServletRequest request) {

	LOGGER.debug("deleting token {}", idTokenString);
	ResponseData dat = (new ResponseData()).setSuccess(false);

	try {
	    final SubjectData userValues = TokenService.parseToData(idTokenString);
	    final String subject = userValues.getSubject();

	    deactivateToken(subject);
	    dat.setStatus(Status.OK);

	} catch (InvalidJwtException e) {
	    e.printStackTrace();
	    throw new javax.ws.rs.BadRequestException(e.getCause());
	}

	Calendar c = Calendar.getInstance();
	c.add(Calendar.MINUTE, 1);
	// Zero, to retire cookie
	final int maxAge = 0;

	return
		doPOST(dat)
		.cookie(genCookie("", jwtConf, request, maxAge, c.getTime()))
		.build();
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
                if (jwtConf.getTokenType().equalsIgnoreCase(method)) {
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
	final boolean secure = conf.isCookieSecure();
	boolean httpOnly = conf.isCookieHttpOnly();
	
	return
		new NewCookie(
			name, value, path, domain, NewCookie.DEFAULT_VERSION,
			comment, maxAge, expiry, secure, httpOnly);

/*
        NewCookie(String name, String value, String path, String domain, int version, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly)

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

    private void updateUser(Map<String, Object> userValues, Token beanToken) {
	final User user = beanToken.getUser();
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
    
	for(String rname : RoleNames.MEMBER) {
	    u.addRole(roleDAO.findByName(rname).get());
	}
	return u;
    }

}

