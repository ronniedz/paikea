package cab.bean.srvcs.tube4kids.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;

@Path("/oauth2")
@Produces(MediaType.APPLICATION_JSON)
public class GoogleAuthNResource extends BaseResource {

    // @ PermitAll
    @POST
    @Path("callback")
    // Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response callback(String idToken) {
//    public Response callback(@QueryParam("id_token") String idToken, @Context SecurityContext context) {
//        User user = (User) context.getUserPrincipal();
	ResponseData dat = new ResponseData(idToken);

	return doPOST(dat.setSuccess(idToken != null).setEntity(idToken)).build();

    }
}
