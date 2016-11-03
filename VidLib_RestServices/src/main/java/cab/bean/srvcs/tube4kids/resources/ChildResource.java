package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.LongParam;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.db.ChildDAO;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;

@Path("/child")
@Produces(MediaType.APPLICATION_JSON)
public class ChildResource {

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
	
	List<Long>ids = new ArrayList<Long>();
	for (int i = 0; i < children.size(); i++) {
	    children.get(i).setUserId(fakeUserId);
	    ids.add(childDAO.quickCreate(children.get(i)));
//	    ids.add(childDAO.create(children.get(i)));
	}
	URI location = UriBuilder.fromUri("/child/" +  ids.get(0) ).build();
	
	return Response.status(Response.Status.CREATED)
		.header("Location", location)
		.entity(
			ImmutableMap.<String, Object>builder()
			.put("uri", location)
			.put("ids", ids)
			.build()
		)
	.build();
    }


    private URI getView(long id) {
	return UriBuilder.fromUri("/child/" + id).build();
//	return UriBuilder.fromResource(this.getClass()).path(id +"").build();
//	return null;
    }


    @Path("/{cid}")
    @GET
    @UnitOfWork
    public Optional<Child> viewChild(@PathParam("cid") LongParam cid) {
        return childDAO.findById(cid.get());
    }
    
    
    @Path("/{cid}/pl/{pid}")
    @PATCH
    @UnitOfWork
    public Response childPlaylist(@PathParam("cid") LongParam cid, @PathParam("pid") LongParam pid) {
	Long pidLong = pid.get();
	Long cidLong = cid.get();
	Child child = childDAO.findById(cidLong).get();
	
	child.getPlaylists().add(playlistDAO.findById(pidLong).get());
	
	// Update the Child
	childDAO.create(child);
	
	URI location = UriBuilder.fromUri("/child/" +  cidLong ).build();
	
	return Response.status(Response.Status.CREATED)
		.header("Location", location)
		.entity(
			ImmutableMap.<String, Object>builder()
			.put("uri", location)
			.build()
		)
	.build();
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deleteChild(@PathParam("id") Long id) {
    	childDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }
    
//    @POST
//    @UnitOfWork
//    public Child createChild(Child child) {
//        return childDAO.create(child);
//    }

    @GET
    @UnitOfWork
    public List<Child> listChildren() {
        return childDAO.findAll();
    }

}
