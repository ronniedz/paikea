package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.AgeGroup;
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

@Path("/age")
@Produces(MediaType.APPLICATION_JSON)
public class AgeGroupResource {

    private final AgeGroupDAO ageGroupDAO;

    public AgeGroupResource(AgeGroupDAO ageGroupDAO) {
        this.ageGroupDAO = ageGroupDAO;
    }

    @POST
    @UnitOfWork
    @PermitAll
    public AgeGroup createAgeGroup(AgeGroup ageGroup) {
        return ageGroupDAO.create(ageGroup);
    }

    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({"MANAGER"})
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
