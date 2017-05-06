package cab.bean.srvcs.tube4kids.auth;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.Optional;

import org.hibernate.FlushMode;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.TokenDAO;


public class JWTAuthenticator implements Authenticator<JwtContext, User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticator.class);

    private final TokenDAO tokenDAO;

    public JWTAuthenticator(TokenDAO tokenDAO) {
	this.tokenDAO = tokenDAO;
    }

    @UnitOfWork(readOnly=true, flushMode=FlushMode.MANUAL, transactional=false)
    @Override
    public Optional<User> authenticate(JwtContext context) {
	
        try {
            return tokenDAO.findUserByActiveSubject(context.getJwtClaims().getSubject());
        }
        // All JsonWebTokenExceptions will result in a 401 Unauthorized response.
        catch (MalformedClaimException e) { return Optional.empty(); }
    }
}


