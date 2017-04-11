package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/protected")
@Produces(MediaType.TEXT_PLAIN)
public class ProtectedResource {

    @RolesAllowed({Role.Names.MEMBER_ROLE, Role.Names.ADMIN_ROLE})
    @GET
    public String showSecret(@Context SecurityContext context) {
	User user = (User) context.getUserPrincipal();
        return String.format("Hey there, %s. You know the secret! %d", user.getName(), user.getId());
    }

    @RolesAllowed({Role.Names.MEMBER_ROLE, Role.Names.ADMIN_ROLE})
    @GET
    @Path("admin")
    public String showAdminSecret(@Auth User user) {
//	    User user = (User) context.getUserPrincipal();
        return String.format("Hey there, %s. It looks like you are an admin. %d", user.getName(), user.getId());
    }

}
