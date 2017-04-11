package cab.bean.srvcs.tube4kids.auth;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.auth.Authorizer;

public class AccessAuthorizer implements Authorizer<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessAuthorizer.class);

    @Override
    public boolean authorize(User user, String role) {
        return user.hasRole(role);
    }
}
