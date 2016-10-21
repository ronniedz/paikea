package cab.bean.srvcs.tube4kids.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import cab.bean.srvcs.tube4kids.core.User;

@Path("/gapi/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class GoogleAuthNResource {

    @PermitAll
    @GET
    @Path("callback")
    public String callback(@QueryParam("id_token") String idToken, @Context SecurityContext context) {
//        User user = (User) context.getUserPrincipal();
        return String.format("Got this idToken: %d", idToken);
    }
}
