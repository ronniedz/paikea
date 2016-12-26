package cab.bean.srvcs.tube4kids.resources;

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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;

@Path("/child")
@Produces(MediaType.APPLICATION_JSON)
public class ChildResource extends BaseResource {

    private final long fakeUserId = 1L;
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
    public Response createChild(List<Child> children) {
        final boolean ifMini = isMinimalRequest();
    
        List<?> outp = children.stream().map( aChild -> {
            	aChild.setUserId(fakeUserId); // TODO add real user
            	aChild.setCreated(new Timestamp(java.lang.System.currentTimeMillis()));
            	return childDAO.create(aChild, ifMini);
    	}).collect(Collectors.toList());
        
        ResponseData dat = new ResponseData()
        	.setSuccess(outp != null)
        	.setEntity(outp);
    
        return reply(dat);
    }
    
    @Path("/{cid}")
    @GET
    @UnitOfWork
    public Response viewChild(@PathParam("cid") Long cid) {
	
	Child child = childDAO.findById(cid).orElse(null);
	
	ResponseData dat = new ResponseData()
	.setSuccess(child != null)
	.setEntity(child);

        return reply(dat);
    }
    
    
    @Path("/{cid}/pl/{pid}")
    @PATCH
    @UnitOfWork
    public Response childPlaylist(@PathParam("cid") Long cid, @PathParam("pid") Long pid) {
	ResponseData dat = new ResponseData();
	try {
	    Child child = childDAO.findById(cid).get();
	    Playlist pl = playlistDAO.findById(pid).orElse(null);
	    
	    if (pl == null || child == null) {
		dat.setSuccess(false).setStatus(Response.Status.PRECONDITION_FAILED);
	    } else {
		child.getPlaylists().add(pl);
		dat.setEntity(childDAO.create(child, isMinimalRequest())); // Update the Child
	    }
	} catch (Exception nsee) {
	    dat.setSuccess(false).setStatus(Response.Status.BAD_REQUEST);
	}
	return reply(dat);
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deleteChild(@PathParam("id") Long id) {
	Child child = childDAO.delete(id);
	ResponseData dat = new ResponseData().setSuccess(child != null);
	if (! isMinimalRequest()) {
		dat.setEntity(child);
	}
	return reply(dat);
    }
    
//    @POST
//    @UnitOfWork
//    public Child createChild(Child child) {
//        return childDAO.create(child);
//    }

    @GET
    @UnitOfWork
    public Response listChildren() {
        List<Child> list = childDAO.findAll();
	return reply(new ResponseData(list).setSuccess(list != null));
    }

}
