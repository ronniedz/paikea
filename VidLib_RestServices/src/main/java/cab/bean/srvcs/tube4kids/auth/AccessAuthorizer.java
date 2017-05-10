package cab.bean.srvcs.tube4kids.auth;

import io.dropwizard.auth.Authorizer;
import cab.bean.srvcs.tube4kids.core.User;

public class AccessAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user.hasRole(role);
    }
}
