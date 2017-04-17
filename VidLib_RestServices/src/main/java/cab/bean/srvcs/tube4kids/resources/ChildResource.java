package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.Role.Names;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.RoleDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;

@Path("/child")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({Names.ADMIN_ROLE, Names.GUARDIAN_ROLE, Names.CHILD_EDIT_ROLE })
public class ChildResource extends BaseResource {

//    private final long fakeUserId = 1L;
    private final ChildDAO childDAO;
    private final UserDAO userDAO;
    private final PlaylistDAO playlistDAO;
    private final RoleDAO roleDAO;

    public ChildResource(ChildDAO childDAO, UserDAO userDAO, PlaylistDAO playlistDAO, RoleDAO roleDAO) {
        this.childDAO = childDAO;
        this.userDAO = userDAO;
        this.roleDAO= roleDAO;
        this.playlistDAO = playlistDAO;
    }

    @POST
    @UnitOfWork
    @RolesAllowed({Names.ADMIN_ROLE, Names.GUARDIAN_ROLE, Names.CHILD_EDIT_ROLE, Names.MEMBER_ROLE })
    public Response createChild(List<Child> children, @Auth User user) {
	final boolean ifMini = isMinimalRequest();
        final Timestamp created = new Timestamp(java.lang.System.currentTimeMillis());

        
        if (user.getChildren().isEmpty() && ( ! user.getRoles().contains(new Role(Names.GUARDIAN_ROLE)) ) ) {
	   user.addRole(roleDAO.findByName(Names.GUARDIAN_ROLE).get());
	   userDAO.create(user);
       }
        
        List<?> outp = children.stream().map( aChild -> {
            	aChild.setGuardian(user); 
		aChild.setCreated(created);
            	return childDAO.create(aChild, ifMini);
    	}).collect(Collectors.toList());
        
        return doPOST(
        		new ResponseData().setSuccess(outp != null).setEntity(outp)
        	).build();
    }
    
    @Path("/{cid}")
    @GET
    @UnitOfWork
    @RolesAllowed({Names.ADMIN_ROLE, Names.GUARDIAN_ROLE, Names.CHILD_EDIT_ROLE , Names.CHILD_ROLE})
    public Response viewChild(@Auth User user, @PathParam("cid") Long childId) {

	ResponseData dat = new ResponseData();

	if ( user.hasAnyRole(Names.SUDO) || ( user.hasRole(Names.GUARDIAN_ROLE) && user.isMyChild(childId) ) ) {

	    Child child = childDAO.findById(childId)
        		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child with id [%d] not found", childId)));
	
	    dat = new ResponseData().setSuccess(child != null).setEntity(child);
	}
	return doGET(dat).build();
    }
    
    
    @Path("/{cid}/pl/{pid}")
    @PATCH
    @UnitOfWork
    @RolesAllowed({Names.ADMIN_ROLE, Names.GUARDIAN_ROLE, Names.CHILD_EDIT_ROLE , Names.CHILD_ROLE})
    public Response playlist(@Auth User user, @PathParam("cid") Long childId, @PathParam("pid") Long platlistId) {

	ResponseData dat = new ResponseData();

	if ( user.hasAnyRole(Names.SUDO) || ( user.hasRole(Names.GUARDIAN_ROLE) && user.isMyChild(childId) ) ) {

	    Child child = childDAO.findByIdLoadPlaylists(childId)
		    .orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child with id [%d] not found", childId)));
	
	    Playlist playlist = playlistDAO.findById(platlistId)
		    .orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Playlist with id [%d] not found", platlistId)));

	    child.getPlaylists().add(playlist);
	    child = childDAO.create(child); // Update the Child
	    dat.setSuccess(true).setEntity(isMinimalRequest() ? playlist : child.getPlaylists());
	}
	else {
	    throw new ForbiddenException(String.format("User '%s' has no access to child-[%d]'s records.", user.getFirstname()));
	}
        return doPATCH(dat).build();
    }
    
    
    @Path("/{cid}/pl/{pid}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({Names.ADMIN_ROLE, Names.GUARDIAN_ROLE, Names.CHILD_EDIT_ROLE , Names.CHILD_ROLE})
    public Response dePlaylist(@Auth User user, @PathParam("cid") Long childId, @PathParam("pid") Long playlistId) {
	ResponseData dat = new ResponseData();

	if ( user.hasAnyRole(Names.SUDO) || ( user.hasRole(Names.GUARDIAN_ROLE) && user.isMyChild(childId) ) ) {

	    Child child = childDAO.findByIdLoadPlaylists(childId)
		    .orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child with id [%d] not found", childId)));
	
	    Playlist playlist = playlistDAO.findById(playlistId)
		    .orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Playlist with id [%d] not found", playlistId)));
	    
	    boolean flagSuccess = child.getPlaylists().remove(playlist);
	    // Update the Child
	    child = childDAO.create(child);
	    
	    dat.setSuccess(flagSuccess).setEntity(isMinimalRequest() ? null : child.getPlaylists());
	}
	else {
	    throw new ForbiddenException(String.format("User '%s' has no access to child-[%d]'s records.", user.getFirstname()));
	}
	return doPATCH(dat).build();
    }
    
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @RolesAllowed( { Names.ADMIN_ROLE, Names.GUARDIAN_ROLE, Names.CHILD_EDIT_ROLE } )
    public Response deleteChild(@Auth User user, @PathParam("id") Long id) {

	ResponseData resData = new ResponseData().setSuccess(false);
	Optional<Child> subjectChildOpt = childDAO.findById(id);

	if (subjectChildOpt.isPresent()) {
	    if (  user.hasAnyRole(Names.ADMIN) || subjectChildOpt.get().getGuardian().getId() == user.getId()) {

		Child child = childDAO.delete(subjectChildOpt.get());
		resData
		  	.setSuccess(true)
		  	.setEntity(isMinimalRequest() ? null : child);
	    }
	    else {
		    resData.setStatus(Response.Status.FORBIDDEN);
	    }
	}
	else {
		resData.setStatus(Response.Status.NOT_FOUND);
	}
	return doDELETE(resData).build();
    }
    
//    @POST
//    @UnitOfWork
//    public Child createChild(Child child) {
//        return childDAO.create(child);
//    }

    @GET
    @UnitOfWork
    @RolesAllowed({Names.ADMIN_ROLE, Names.GUARDIAN_ROLE } )
    public Response listChildren(@Auth User user) {
        List<Child> list = childDAO.findAll();
	return doGET(new ResponseData(list).setSuccess(list != null)).build();
    }

}
