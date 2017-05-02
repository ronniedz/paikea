package cab.bean.srvcs.tube4kids.auth;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import cab.bean.srvcs.tube4kids.auth.RoleNames;



@Documented
@Retention (RUNTIME)
@Target({TYPE, METHOD})
public @interface AdminOrOwner {
    String[] adminRoles() default { RoleNames.SUDO_ROLE, RoleNames.ADMIN_ROLE };
    String[] provoPropria();
}
