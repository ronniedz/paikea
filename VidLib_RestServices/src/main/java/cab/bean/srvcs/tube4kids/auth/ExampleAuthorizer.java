package cab.bean.srvcs.tube4kids.auth;

import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.auth.Authorizer;

public class ExampleAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        if(user.getRoles() != null && user.getRoles().contains(role)) {
            return true;
        }

        return false;
    }
}
