package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.core.AgeGroup;
import cab.bean.srvcs.tube4kids.db.AgeGroupDAO;

@Path("/age")
@Produces(MediaType.APPLICATION_JSON)
public class AgeGroupResource {

    private final AgeGroupDAO ageGroupDAO;

    public AgeGroupResource(AgeGroupDAO ageGroupDAO) {
        this.ageGroupDAO = ageGroupDAO;
    }

    @POST
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.CONTENT_MODERATOR_ROLE, RoleNames.UI_MANAGER_ROLE})
    public AgeGroup createAgeGroup(AgeGroup ageGroup) {
        return ageGroupDAO.create(ageGroup);
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.CONTENT_MODERATOR_ROLE, RoleNames.UI_MANAGER_ROLE})
    public Response deleteAgeGroup(@PathParam("id") Long id) {
    	ageGroupDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @UnitOfWork
    public List<AgeGroup> listAges() {
        return ageGroupDAO.findAll();
    }
}
