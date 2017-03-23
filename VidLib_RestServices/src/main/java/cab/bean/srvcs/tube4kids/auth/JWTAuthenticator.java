package cab.bean.srvcs.tube4kids.auth;

import org.apache.commons.beanutils.BeanUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import cab.bean.srvcs.tube4kids.db.TokenDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;
import static java.math.BigDecimal.ONE;


public class JWTAuthenticator implements Authenticator<JwtContext, User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticator.class);

    private TokenDAO tokenDAO;
    private UserDAO userDAO;

    public JWTAuthenticator(TokenDAO tokenDAO, UserDAO userDao) {
	this.tokenDAO = tokenDAO;
	this.userDAO = userDao;
    }

    @UnitOfWork
    @Override
    public Optional<User> authenticate(JwtContext context) {
	
	LOGGER.debug("authenticate");
        // Provide your own implementation to lookup users based on the principal attribute in the
        // JWT Token. E.g.: lookup users from a database etc.
        // This method will be called once the token's signature has been verified

        // In case you want to verify different parts of the token you can do that here.
        // E.g.: Verifying that the provided token has not expired.

        // All JsonWebTokenExceptions will result in a 401 Unauthorized response.

        try {
            
            final String subject = context.getJwtClaims().getSubject();

//            Optional<User> u = tokenDAO.findUserWithToken(subject);
            Optional<User> u =  userDAO.findById(1L);

            LOGGER.debug("got subject: " + subject);
//            LOGGER.debug("ignoring found user" + u);
            if (true) {
//            if (! u.isPresent()) {
        	
        		u = Optional.of(createSSOUser( context.getJwtClaims() ));

            }
    	LOGGER.debug("setting user " + u.toString());
            return u;
        }
        catch (MalformedClaimException e) { return Optional.empty(); }
    }

/*    
picture": "https://lh4.googleusercontent.com/-6zyoEuugOe0/AAAAAAAAAAI/AAAAAAAAAAA/AAomvV1S-oO0wsV53-afYLt9AdexWebGVQ/s96-c/photo.jpg",
"aud": "16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com",
"family_name": "Admin",
"iss": "accounts.google.com",
"email_verified": true,
"name": "Atozebra Admin",
"at_hash": "FJ_mmjQ3zrqCmXAklLsq7A",
"given_name": "Atozebra",
"exp": 1489881248,
"azp": "16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com",
"iat": 1489877648,
"locale": "en",
"email": "a2zlangmgr@gmail.com",
"sub": "112676373123228150764"
*/
    
    private User createSSOUser(JwtClaims jwtClaims)  throws MalformedClaimException {
        User u = new User();
	final  Map<String, Object> m = new Hashtable<String, Object>();
        
	jwtClaims.getClaimsMap().entrySet().forEach(x -> { m.put(x.getKey(), x.getValue()); });
        System.err.println(m); 

        //        claims.setClaim("picture",idToken.getPayload().getUnknownKeys().get("picture")); // additional claims/attributes about the subject can be added

        u.setEmail(jwtClaims.getStringClaimValue("email"));
        u.setFirstname(jwtClaims.getStringClaimValue("firstname"));
        u.setLastname(jwtClaims.getStringClaimValue("lastname"));
        u.setEmailVerified((Boolean)jwtClaims.getClaimValue("email_verified"));
        u.setEmailVerified(Boolean.TRUE);
        u.setActivated(Boolean.TRUE);
        
	u.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));

       return userDAO.create(u);
    }
    
    private void copyProperties(User u, JwtClaims jwtClaims) throws MalformedClaimException {
	
//	final  Map<String, Object> m = new Hashtable<String, Object>();
//        jwtClaims.getClaimsMap().entrySet().forEach(x -> { System.err.println(x.getKey()); m.put(x.getKey(), x.getValue()); });
//        BeanUtils.copyProperties(u, m);
    }
}


