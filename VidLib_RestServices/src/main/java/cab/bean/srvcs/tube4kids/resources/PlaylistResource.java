package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.LongParam;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Video;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;

@Path("/playlist")
@Produces(MediaType.APPLICATION_JSON)
public class PlaylistResource {

    // TODO JWT auth
    private final long fakeUserId = 1;
    private final PlaylistDAO playlistDAO;
    private final VideoDAO videoDAO;
    

    public PlaylistResource(PlaylistDAO playlistDAO, VideoDAO videoDAO) {
	this.playlistDAO = playlistDAO;
	this.videoDAO = videoDAO;
    }

    @POST
    @UnitOfWork
    public Response createPlaylist(Playlist playlist) {
	
	final UriBuilder uriBuilder = UriBuilder.fromResource(PlaylistResource.class);
	
	// TODO JWT auth
	playlist.setUserId(fakeUserId);
	
	long pid = playlistDAO.create(playlist).getId();
	
	return Response.seeOther( uriBuilder.path(Long.toString(pid)).build() )
		.status(Response.Status.CREATED).build();
    }

    @GET
    @UnitOfWork
    public List<Playlist> listPlaylists() {
	return playlistDAO.findAll();
    }

    @Path("/{pid}")
    @GET
    @UnitOfWork
    public Optional<Playlist> viewPlaylist(@PathParam("pid") LongParam pid) {
	return playlistDAO.findById(pid.get());
    }

    @Path("/{pid}")
    @PATCH
    @UnitOfWork
    public Response addVideos(@PathParam("pid") LongParam pid, Set<String> videoIds) {
	Long pidVal = Optional.of(pid.get()).orElse(0L);
	
	Playlist p = playlistDAO.findById(pidVal).get();
	
	Set<Video> videos = p.getVideos(); 
	
	for (String vid : videoIds ) {
	    videos.add(videoDAO.findById(vid).get());
	}
	playlistDAO.create(p);
	
	URI location = UriBuilder.fromUri("/playlist/" + pidVal).build();
	return Response
		.status(Response.Status.CREATED)
		.header("Location", location)
		.entity(ImmutableMap.<String, URI> builder()
			.put("Created", location).build()).build();
    }

    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deletePlaylist(@PathParam("id") Long id) {
    	playlistDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/user/{userId: [0-9]+}")
    @GET
    @UnitOfWork
    public List<Playlist> listPlaylists(@PathParam("userId") LongParam userId) {
	return playlistDAO.findUserLists(userId.get());
    }

}
