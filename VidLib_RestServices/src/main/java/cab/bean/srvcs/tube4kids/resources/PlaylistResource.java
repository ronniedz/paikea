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
import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Video;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;

@Path("/playlist")
@Produces(MediaType.APPLICATION_JSON)
public class PlaylistResource extends BaseResource {

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
	
	// TODO JWT auth
	playlist.setUserId(fakeUserId);
	
	Playlist p = playlistDAO.create(playlist);
	
	boolean respBody =  (p != null);
	ResponseData dat = new ResponseData()
		.setSuccess(respBody)
		.setEntity( respBody ? isMinimalRequest() ? p.getId() : p : null);
        return doPOST(dat).build();
    }

    @GET
    @UnitOfWork
    public Response listPlaylists() {
	 List<Playlist> list = playlistDAO.findAll();
	return doGET(new ResponseData(list).setSuccess(list != null)).build();
    }


//    @Path("/{pid: [0-9]+}")
//    @GET
//    @UnitOfWork
//    public Response viewPlaylist(@PathParam("pid") Long id) {
//	Playlist o = playlistDAO.findById(id).orElse(null);
//	return reply(new ResponseData(o).setSuccess(o != null));
//    }
//


    @Path("/user/{userId: [0-9]+}")
    @GET
    @UnitOfWork
    public Response listUserPlaylists(@PathParam("userId") LongParam userId) {
	return doGET(
		new ResponseData( playlistDAO.findUserLists(userId.get())).setSuccess(true)
	).build();
    }

//    @Path("/pid: [0-9]+}")
//    @PATCH
//    @UnitOfWork
//    public Response updatePlaylist(@PathParam("pid") Long pid, Playlist objectData) {

    /** Update **/
    @PATCH
    @UnitOfWork
    public Response updatePlaylist(Playlist objectData) {
	
	
	Playlist o = playlistDAO.update(objectData);
	
	ResponseData dat = new ResponseData()
		.setSuccess(o != null)
		.setEntity(isMinimalRequest() ? null : o );
	
	if ( o != null) {
	    dat.setLocation( UriBuilder.fromResource(this.getClass()).path(o.getId().toString()).build() );
	}

        return doPATCH(dat).build();
    }
    

    @Path("/video/{pidVal: [0-9]+}")
    @PATCH
    @UnitOfWork
    public Response addVideos(@PathParam("pidVal") Long pidVal, Set<String> videoIds) {
	ResponseData dat = new ResponseData().setSuccess(false);

	try {
            Playlist p = playlistDAO.findById(pidVal).orElse(null);
            
            if ( p == null ) {
                dat .setStatus(Response.Status.NOT_FOUND);
            }
            else {
                	Set<Video> videos = p.getVideos(); 
                	
                	try {
                    	for (String vid : videoIds ) {
                    	    videos.add(videoDAO.findById(vid).get());
                    	}
                    	p = playlistDAO.create(p);
                    	dat.setSuccess(p != null).setEntity(isMinimalRequest() ? null : p);
                	}
                	catch (java.util.NoSuchElementException nsee) {
                	    dat
                	    .setStatus(Response.Status.NOT_FOUND)
                	    .setErrorMessage("Video not in library");
                	}
            }
	} catch (Exception nsee) {
	    dat.setStatus(Response.Status.BAD_REQUEST);
	}
        return doPATCH(dat).build();
    }

    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deletePlaylist(@PathParam("id") Long id) {
	
	Playlist o = playlistDAO.delete(id);
	
	ResponseData dat = new ResponseData()
		.setSuccess(o != null)
		.setEntity(isMinimalRequest() ? null : o );

        return doDELETE(dat).build();
    }

}
