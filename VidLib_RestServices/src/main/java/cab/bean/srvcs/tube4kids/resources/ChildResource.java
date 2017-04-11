package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.IntParam;
import io.dropwizard.jersey.params.LongParam;
import io.dropwizard.jersey.params.BooleanParam;

import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;

@Path("/child")
@Produces(MediaType.APPLICATION_JSON)
public class ChildResource extends BaseResource {

//    private final long fakeUserId = 1L;
    private final ChildDAO childDAO;
    private final VideoDAO videoDAO;
    private final PlaylistDAO playlistDAO;

    public ChildResource(ChildDAO childDAO, VideoDAO videoDAO, PlaylistDAO playlistDAO) {
        this.childDAO = childDAO;
        this.videoDAO = videoDAO;
        this.playlistDAO = playlistDAO;
    }

    @POST
    @UnitOfWork
    @RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.GUARDIAN_ROLE, Role.Names.CHILD_EDIT })
    public Response createChild(List<Child> children, @Auth User user) {
        final boolean ifMini = isMinimalRequest();
    
        List<?> outp = children.stream().map( aChild -> {
            	aChild.setParent(user); 
            	aChild.setCreated(new Timestamp(java.lang.System.currentTimeMillis()));
            	return childDAO.create(aChild, ifMini);
    	}).collect(Collectors.toList());
        
        ResponseData dat = new ResponseData()
        	.setSuccess(outp != null)
        	.setEntity(outp);
    
        return doPOST(dat).build();
    }
    
    @Path("/{cid}")
    @GET
    @UnitOfWork
    @RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.GUARDIAN_ROLE, Role.Names.CHILD_EDIT , Role.Names.CHILD_ROLE})
    public Response viewChild(@Auth User user, @PathParam("cid") Long cid) {
	
	Child child = childDAO.findById(cid).orElse(null);
	
	ResponseData dat = new ResponseData()
	.setSuccess(child != null)
	.setEntity(child);

        return doGET(dat).build();
    }
    
    
    @Path("/{cid}/pl/{pid}")
    @PATCH
    @UnitOfWork
    @RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.GUARDIAN_ROLE, Role.Names.CHILD_EDIT , Role.Names.CHILD_ROLE})
    public Response playlist(@Auth User user, @PathParam("cid") Long cid, @PathParam("pid") Long pid) {
	ResponseData dat = new ResponseData();

	Optional<Child > childOpt = user.getChildren().stream().filter(child -> cid.equals(child.getId()) ).findFirst();
	
	if (childOpt.isPresent()) {

	    Playlist pl = null;
	    
	    if ( (pl = playlistDAO.retrieve(pid)) != null) {
		
		pl.setUser(user);

//		Child child = childOpt.get();
		Child child = childDAO.findChildWIthPlaylists(cid).get();
		
//		System.out.println("Child: "+ child);

		child.getPlaylists().add(pl);
//		child.addPlaylist(pl);
//		Set<Playlist> childLists = childDAO.getPlaylistOf(child);
//		childLists.add(pl);
//		child.setPlaylists(childLists);
		child = childDAO.create(child); // Update the Child
		dat.setSuccess(true).setEntity(isMinimalRequest() ? pl : child.getPlaylists());
	    }
	    else {
		dat
		.setSuccess(false).setStatus(Response.Status.NOT_FOUND)
		.setErrorMessage(String.format("Playlist with id [%d] not found", pid))
		;
	    }
	} else {
	    dat.setSuccess(false)
	    .setErrorMessage(String.format("Child with id [%d] not found", cid))
	    .setStatus(Response.Status.BAD_REQUEST);
	}
        return doPATCH(dat).build();

//	try {
//	    Child child = childDAO.findById(cid).get();
//	    Playlist pl = rc.getResource(PlaylistResource.class).liberatePlaylist(pid);
//	    if (pl == null || child == null) {
//		dat.setSuccess(false).setStatus(Response.Status.NOT_FOUND);
//	    } else {
//		pl.setUser(child.getParent());
////		child.getPlaylists().add(pl);
//		child = childDAO.create(child); // Update the Child
//		dat.setSuccess(child != null).setEntity(isMinimalRequest() ? pl : pl);
//	    }
//	} catch (Exception nsee) {
//	    dat.setSuccess(false).setStatus(Response.Status.BAD_REQUEST);
//	}
//        return doPATCH(dat).build();
    }
    
    
    @Path("/{cid}/pl/{pid}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.GUARDIAN_ROLE, Role.Names.CHILD_EDIT , Role.Names.CHILD_ROLE})
    public Response dePlaylist(@Auth User user, @PathParam("cid") Long cid, @PathParam("pid") Long pid) {
	ResponseData dat = new ResponseData();
//	Optional<Child > childOpt = user.getChildren().stream().filter(child -> cid.equals(child.getId()) ).findFirst();
//	
//	if (childOpt.isPresent()) {
//
//	    Playlist pl = null;
//	    
//	    if ( (pl = playlistDAO.retrieve(pid)) != null) {
//		
//	    }
//	    
//	}
	try {
	    Child child = childDAO.findById(cid).get();
	    Playlist pl = playlistDAO.retrieve(pid);
	    if (pl == null || child == null) {
		dat.setSuccess(false).setStatus(Response.Status.NOT_FOUND);
	    } else {
//		child.getPlaylists().remove(pl);
		child = childDAO.create(child); // Update the Child
		dat.setSuccess(child != null).setEntity(isMinimalRequest() ? pl : pl);
	    }
	} catch (Exception nsee) {
	    dat.setSuccess(false).setStatus(Response.Status.BAD_REQUEST)
	    .setErrorMessage(nsee.getMessage());
	}
	return doPATCH(dat).build();
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.GUARDIAN_ROLE, Role.Names.CHILD_EDIT })
    public Response deleteChild(@Auth User user, @PathParam("id") Long id) {
	Child child = childDAO.delete(id);
	ResponseData dat = new ResponseData().setSuccess(child != null);
	dat.setEntity(isMinimalRequest() ? null : child);
        return doDELETE(dat).build();
    }
    
//    @POST
//    @UnitOfWork
//    public Child createChild(Child child) {
//        return childDAO.create(child);
//    }

    @GET
    @UnitOfWork
    @RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.GUARDIAN_ROLE})
    public Response listChildren(@Auth User user) {
        List<Child> list = childDAO.findAll();
	return doGET(new ResponseData(list).setSuccess(list != null)).build();
    }

}
