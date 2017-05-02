package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.AgeGroup;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.db.AgeGroupDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

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

import java.util.List;
import cab.bean.srvcs.tube4kids.auth.RoleNames;

@Path("/age")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.CONTENT_MODERATOR_ROLE, RoleNames.UI_MANAGER_ROLE})
public class AgeGroupResource {

    private final AgeGroupDAO ageGroupDAO;

    public AgeGroupResource(AgeGroupDAO ageGroupDAO) {
        this.ageGroupDAO = ageGroupDAO;
    }

    @POST
    @UnitOfWork
    public AgeGroup createAgeGroup(AgeGroup ageGroup) {
        return ageGroupDAO.create(ageGroup);
    }

    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deleteAgeGroup(@PathParam("id") Long id) {
    	ageGroupDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @UnitOfWork
    @PermitAll
    public List<AgeGroup> listAges() {
        return ageGroupDAO.findAll();
    }

}
