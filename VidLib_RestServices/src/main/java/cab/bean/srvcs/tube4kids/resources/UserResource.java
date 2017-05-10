package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

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
import java.util.Optional;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.MEMBER_ROLE})
public class UserResource {

    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
   public Response deleteUser(@Auth User user, @PathParam("id") Long id) {
    	userDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/{id}")
    @GET
    @UnitOfWork
    public Optional<User> viewUser(@PathParam("id") LongParam id) {
	return userDAO.findById(id.get());
    }

    @Path("/self")
    @GET
    @UnitOfWork
    public Optional<User> self(@Auth User user) {
	return userDAO.findById(user.getId());
    }
    
    @POST
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.USER_MANAGER_ROLE}) 
    public User createUser(User user) {
        return userDAO.create(user);
    }

    @GET
    @UnitOfWork
    public List<User> listUsers() {
        return userDAO.findAll();
    }
}
