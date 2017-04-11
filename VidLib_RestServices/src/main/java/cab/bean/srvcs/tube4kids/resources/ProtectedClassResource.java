package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * {@link RolesAllowed}, {@link PermitAll} are supported on the class level.<p>
 * Method level annotations take precedence over the class level ones
 */

@Path("/protectedClass")
@RolesAllowed({Role.Names.MEMBER_ROLE})
public final class ProtectedClassResource {

    @GET
    @PermitAll
    @Path("guest")
    public String showSecret(@Auth User user) {
        return String.format("Hey there, %s. You know the secret! %d", user.getName(), user.getId());
    }

    /* Access to this method is authorized by the class level annotation */
    @GET
    public String showBasicUserSecret(@Context SecurityContext context) {
        User user = (User) context.getUserPrincipal();
        return String.format("Hey there, %s. You seem to be a basic user. %d", user.getName(), user.getId());
    }

    @GET
    @RolesAllowed(Role.Names.ADMIN_ROLE)
    @Path("admin")
    public String showAdminSecret(@Auth User user) {
        return String.format("Hey there, %s. It looks like you are an admin. %d", user.getName(), user.getId());
    }

}
