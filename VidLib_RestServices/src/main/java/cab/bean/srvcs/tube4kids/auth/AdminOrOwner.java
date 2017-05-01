package cab.bean.srvcs.tube4kids.auth;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;



@Documented
@Retention (RUNTIME)
@Target({TYPE, METHOD})
public @interface AdminOrOwner {
    String[] admins() default "";
    String member();
    String bindParam();
}
