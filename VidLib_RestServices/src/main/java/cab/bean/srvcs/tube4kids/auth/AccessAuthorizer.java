package cab.bean.srvcs.tube4kids.auth;

import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.auth.Authorizer;

public class AccessAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return (user.getRoles() != null && user.getRoles().contains(role)) ;
    }
}
