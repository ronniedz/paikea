package cab.bean.srvcs.tube4kids.auth;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import io.dropwizard.auth.Auth;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.User;


/**
 * This attribute can be applied to resource <b>methods</b>.
 * The {@code adminRoles} property is a list of security role names.
 * <p/>
 * The attribute {@code provoPropria}, is a list consisting of colon separated pairs:
 * <blockquote>
 * <code>
 * 	{ <b>user-method</b>:<b>operandReference</b>, ... }
 * </code>
 * </blockquote>
 * <p/>
 * The <b><code>operandReference</code></b> is a reference to the annotated method's parameters. It points to the object on which the 
 * ownership test (<b><code>user-method</code></b>) will be applied.
 * <p/>
 * The <b><code>user-method</code></b> is a method on the User object that returns a boolean representing whether the
 * user <i>owns</i> the supplied object. A test method takes the form:
 * <blockquote>
 * <code>
 * 	Boolean isMyEntity(Entity entity);
 * <p/>
 * OR
 * <p/>
 * 	Boolean isMyEntity( [ Long | String | etc.... ] entityId );
 * </code>
 * </blockquote>
 * 
 * <p/>
 * 
 * The {@code entity or entityId} is determined via the <code>operandReference</code>. There are 3 ways to reference an <i>operand</i> ...
 * Consider the following resource method:
 * <blockquote>
 * <pre>
@AdminOrOwner(
 adminRoles={RoleNames.ADMIN_ROLE, RoleNames.SUDO_ROLE},
 provoPropria= {"isMyChild:<b>arg0</b>"}
)
public Response updateChild(Child aChild, @Auth User user) { ...
 * </pre>
 * </blockquote>
 * 
 * <ol>
 * <li>
 * Refer to the intended operand via index to the resource method arguments.
 * <b>arg0</b>, the method'a first argument, refers to the parameter <code>aChild</code> and will result in execution of method:
 * <p/>
 * <blockquote>
 * <code>
 * 	Boolean isOwner = user.isMyChild(aChild); 
 * </code>
 * </blockquote>
 * </li>
 * <li>
 * The same results by replacing <b>arg0</b> with just <b>0</b>.
 * <blockquote>
 * <code>
 * 	{"isMyChild:<b>0</b>"}
 * </code>
 * </blockquote>
 * </li>
 * <li>
 * Operands may also be referenced via the {@link @PathParam} value. Consider...:
 * <blockquote>
 * <pre>
@AdminOrOwner(
 adminRoles={RoleNames.ADMIN_ROLE, RoleNames.SUDO_ROLE},
 provoPropria= {"isMyChild:<b>cid</b>", "isMyPlaylist:<b>pid</b>"})
public Response playlist(@PathParam("<b>cid</b>") Long childId, @PathParam("<b>pid</b>") Long playlistId, @Auth User user) { ...
 * </pre>
 * <b>cid</b> refers to the parameter <code>childId</code>:
 * <br/>
 * <b>pid</b> refers to the parameter <code>playlistId</code>
 * </blockquote>
 * <br/>
 * The resulting logic will be:
 * <p/>
 * <blockquote>
 * <code>
 * 	Boolean isOwner = user.isMyChild(childId) && user.isMyPlaylist(playlistId); 
 * </code>
 * </blockquote>
 * </li>
 * </ol>
 * 
 * TODO -- Allow for Collection/Array <code>operands</code>.  
 */
@Documented
@Retention (RUNTIME)
@Target({TYPE, METHOD})
public @interface AdminOrOwner {
    String[] adminRoles() default { RoleNames.SUDO_ROLE, RoleNames.ADMIN_ROLE };
    String[] provoPropria();
}
