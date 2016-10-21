package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.AgeGroup;
import cab.bean.srvcs.tube4kids.db.AgeGroupDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public AgeGroup createAgeGroup(AgeGroup ageGroup) {
        return ageGroupDAO.create(ageGroup);
    }

    @GET
    @UnitOfWork
    public List<AgeGroup> listAges() {
        return ageGroupDAO.findAll();
    }

}
