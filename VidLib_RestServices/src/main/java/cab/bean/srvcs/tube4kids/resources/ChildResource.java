package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.DenyAll;
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

import cab.bean.srvcs.tube4kids.auth.AdminOrOwner;
import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.RoleDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;

@Path("/child")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.GUARDIAN_ROLE, RoleNames.CHILD_EDIT_ROLE })
public class ChildResource extends BaseResource {

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
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.GUARDIAN_ROLE, RoleNames.CHILD_EDIT_ROLE, RoleNames.MEMBER_ROLE })
    public Response createChild(List<Child> children, @Auth User user) {
	final boolean ifMini = isMinimalRequest();
        final Timestamp created = new Timestamp(java.lang.System.currentTimeMillis());

        if (user.getChildren().isEmpty() && ( ! user.getRoles().contains(new Role(RoleNames.GUARDIAN_ROLE)) ) ) {
	   user.addRole(roleDAO.findByName(RoleNames.GUARDIAN_ROLE).get());
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

    @PATCH
    @UnitOfWork
    @AdminOrOwner(
	    adminRoles={ RoleNames.ADMIN_ROLE, RoleNames.CHILD_EDIT_ROLE},
	    ownerTests= { "isMyChild:arg0"} )
    public Response updateChild(Child aChild, @Auth User user) {
	return
	    doPOST(new ResponseData().setSuccess(true).setEntity( childDAO.update(aChild)))
	    .build();
    }

    @Path("/{cid}")
    @GET
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.GUARDIAN_ROLE, RoleNames.CHILD_EDIT_ROLE , RoleNames.CHILD_ROLE})
    @AdminOrOwner(
	    adminRoles={ RoleNames.ADMIN_ROLE, RoleNames.CHILD_EDIT_ROLE},
	    ownerTests= { "isMyChild:cid"} )
    public Response viewChild(@Auth User user, @PathParam("cid") Long childId) {
	    Child child = childDAO.findById(childId)
        		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child with id [%d] not found", childId)));
	return doGET(new ResponseData().setSuccess(child != null).setEntity(child)).build();
    }

    @Path("/{cid}/pl/{pid}")
    @PATCH
    @UnitOfWork
    @AdminOrOwner(
	    adminRoles={ RoleNames.ADMIN_ROLE, RoleNames.CHILD_EDIT_ROLE},
	    ownerTests= { "isMyChild:cid", "isMyPlaylist:pid"} )
    public Response playlist(@Auth User user, @PathParam("cid") Long childId, @PathParam("pid") Long platlistId) {

	Child child = childDAO.findByIdLoadPlaylists(childId)
		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child with id [%d] not found", childId)));

	Playlist playlist = playlistDAO.findById(platlistId)
		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Playlist with id [%d] not found", platlistId)));

	child.getPlaylists().add(playlist);
	child = childDAO.create(child); // Update the Child
	return doPATCH(new ResponseData()
	    .setSuccess(true).setEntity(isMinimalRequest() ? playlist : child.getPlaylists())
	).build();
    }

    @Path("/{cid}/pl/{pid}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.GUARDIAN_ROLE, RoleNames.CHILD_EDIT_ROLE , RoleNames.CHILD_ROLE})
    @AdminOrOwner(
	adminRoles={ RoleNames.SUDO_ROLE, RoleNames.ADMIN_ROLE, RoleNames.CHILD_EDIT_ROLE},
	ownerTests= { "isMyChild:cid", "isMyPlaylist:pid"} )
    public Response dePlaylist(@Auth User user, @PathParam("cid") Long childId, @PathParam("pid") Long playlistId) {

	Child child = childDAO.findByIdLoadPlaylists(childId)
		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child [%d] not found", childId)));
	Playlist playlist = playlistDAO.findById(playlistId)
		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Playlist [%d] not found", playlistId)));

	boolean flagSuccess = child.getPlaylists().remove(playlist);
	// Update the Child
	child = childDAO.create(child);
	return doPATCH(new ResponseData().setSuccess(flagSuccess).setEntity(isMinimalRequest() ? null : child.getPlaylists())).build();
    }

    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @AdminOrOwner(adminRoles={ RoleNames.ADMIN_ROLE, RoleNames.CHILD_EDIT_ROLE}, ownerTests={ "isMyChild:id"} )
    public Response deleteChild(@Auth User user, @PathParam("id") Long id) {
	ResponseData resData = new ResponseData().setSuccess(false);
	Child child = childDAO.findById(id)
		.orElseThrow(() -> new javax.ws.rs.NotFoundException(String.format("Child with id [%d] not found", id)));
	childDAO.delete(child);
	resData.setSuccess(true).setEntity(isMinimalRequest() ? null : child);
	return doDELETE(resData).build();
    }

    @GET
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.GUARDIAN_ROLE } )
     public Response listChildren(@Auth User user) {
	
	return doGET(
		new ResponseData(
			user.hasAnyRole(RoleNames.ADMIN)
            		? childDAO.findAll()
            		: childDAO.findChildrenOf(user)
		)
		.setSuccess(true)).build();
    }

}
