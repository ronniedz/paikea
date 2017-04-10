package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.LongParam;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.Role.Names;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.Video;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;

@Path("/playlist")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({Names.GUARDIAN_ROLE, Names.MEMBER_ROLE, Names.ADMIN_ROLE, Names.CONTENT_MODERATOR_ROLE,
    Names.SUDO_ROLE}) 
public class PlaylistResource extends BaseResource {

    // TODO JWT auth
//    private final long fakeUserId = 1;
    private final PlaylistDAO playlistDAO;
    private final VideoDAO videoDAO;
    
    
    public PlaylistResource(PlaylistDAO playlistDAO, VideoDAO videoDAO) {
	this.playlistDAO = playlistDAO;
	this.videoDAO = videoDAO;
    }

    @POST
    @UnitOfWork
    public Response createPlaylist(Playlist playlist, @Auth User user) { 
	
	// TODO JWT auth
	playlist.setUserId(user.getId());
	
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


//  @Path("/pid: [0-9]+}")
//  @PATCH
//  @UnitOfWork
//  public Response updatePlaylist(@PathParam("pid") Long pid, Playlist objectData) {

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
    
    @Path("/pick/{pid: [0-9]+}")
    @PUT
    @UnitOfWork
    public Response pickPlaylist(@PathParam("pid") Long pid, Playlist destPlaylist, @Auth User user) {
	// TODO In the future won't need to liberate() as only decoupled playlists will be findable 
	Playlist org = liberatePlaylist(pid);
	if ( destPlaylist.getTitle().isEmpty()) {
	    destPlaylist.setTitle(org.getTitle() + " (picked)");
	}
	destPlaylist.setUserId(user.getId());
	destPlaylist = playlistDAO.create(destPlaylist);
	destPlaylist.setVideos(org.getVideos().stream().map(orgVid -> { 
	    return videoDAO.findById(orgVid.getVideoId()).get();
	}).collect(Collectors.toSet()));

	playlistDAO.create(destPlaylist);
	ResponseData dat = new ResponseData(destPlaylist).setSuccess(destPlaylist != null);
	return doPOST(dat).build();
	
//	o.setTitle(org.getTitle() + " (picked)");
//	o.setUserId(2L);
//	o = playlistDAO.create(o);
//	
//	o.setVideos(org.getVideos().stream().map(orgVid -> { 
//	    return videoDAO.findById(orgVid.getVideoId()).get();
//	}).collect(Collectors.toSet()));
//	
//	playlistDAO.create(o);
//	boolean respBody =  (o != null);
//	ResponseData dat = new ResponseData(o).setSuccess(respBody);
//	return doPOST(dat).build();
    }

    @Path("/liberate/{pid: [0-9]+}")
    @PUT
    @UnitOfWork
    @RolesAllowed({Role.Names.GUARDIAN_ROLE, Role.Names.MEMBER_ROLE}) 
    public Playlist liberatePlaylist(@PathParam("pid") Long pid) {
	
	Playlist org = playlistDAO.retrieve(pid);
	if (org.getUserId() == null) {
	    return org;
	}
	
	Playlist o = new Playlist();

	o.setTitle(org.getTitle() + " (copy)");
	o = playlistDAO.create(o);
	
	o.setVideos(org.getVideos().stream().map(orgVid -> { 
	    return videoDAO.findById(orgVid.getVideoId()).get();
	}).collect(Collectors.toSet()));
	
	return playlistDAO.create(o);
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
    @RolesAllowed({Role.Names.GUARDIAN_ROLE, Role.Names.MEMBER_ROLE}) 
    public Response listUserPlaylists(@PathParam("userId") LongParam userId, @Auth User user) {
	return doGET(new ResponseData( playlistDAO.findUserLists(userId.get())).setSuccess(true)).build();
    }

    
    @Path("/my")
    @GET
    @UnitOfWork
    @RolesAllowed({Role.Names.MEMBER_ROLE}) 
    public Response listOwnPlaylists(@Auth User user) {
	return doGET(new ResponseData( user.getPlaylists()).setSuccess(true)).build();
    }
    
    @Path("/video/{pidVal: [0-9]+}")
    @PATCH
    @UnitOfWork
    @RolesAllowed({Names.GUARDIAN_ROLE, Names.PLAYLIST_MANAGER_ROLE})
    public Response addVideos(@PathParam("pidVal") Long pidVal, Set<String> videoIds, @Auth User user) {
	
	ResponseData dat = new ResponseData().setSuccess(false);
	
	if (  user.getPlaylists().stream().map(Playlist::getId).collect(Collectors.toSet()).contains(pidVal)
	    || user.hasAnyRole(Names.PLAYLIST_MANAGER)
	) {
	    Playlist p = null;
	    if ( (p = playlistDAO.findById(pidVal).orElse(null)) != null ) {
		Tuple t = bulkFindUtil(videoIds);
		p.getVideos().addAll(t.videos);
		p = playlistDAO.create(p); // SaveOrUpdate Playlist
		dat.setSuccess(p != null).setEntity(isMinimalRequest() ? null : p);
		if ( t.delta == 0) {
		    dat.setErrorMessage(String.format("{%d} of selected videos were not found", t.delta));
		}
	    }
	    else {
		dat.setStatus(Response.Status.NOT_FOUND).setErrorMessage("No such playlist");
	    }
	}
	else {
	    dat.setStatus(Response.Status.FORBIDDEN);
	}
        return doPATCH(dat).build();
    }

    private class Tuple {
	  public final int delta; 
	  public final Collection<Video> videos; 
	  public Tuple(int delta, Collection<Video> videos) { 
	    this.delta = delta; 
	    this.videos = videos; 
	  }
    } 

    private Tuple bulkFindUtil(String... videoIds){
	List<Video> foundVids = videoDAO.findWithIds(videoIds);
	return new Tuple(videoIds.length - foundVids.size(), foundVids);
    }
    
    private Tuple bulkFindUtil(Collection<String> videoIds){
	List<Video> foundVids = videoDAO.findWithIds(videoIds);
	return new Tuple(videoIds.size() - foundVids.size(), foundVids);
    }
    /**
     * Video ids can be sngle or a list of ids. Video ids may be separated by:
     * commas, pipes, semicolons or spaces.
     * 
     * @param pidVal
     * @param videoIds
     * @return
     */
    @Path("{pidVal: [0-9]+}/v/{videoIds}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({Names.GUARDIAN_ROLE, Names.PLAYLIST_MANAGER_ROLE})
    public Response dropVideos(@PathParam("pidVal") Long pidVal, @PathParam("videoIds") String videoIds, @Auth User user) {
	ResponseData dat = new ResponseData().setSuccess(false);
	
	if (  user.getPlaylists().stream().map(Playlist::getId).collect(Collectors.toSet()).contains(pidVal)
	    || user.hasAnyRole(Names.PLAYLIST_MANAGER)
	) {
	    Playlist p = null;
	    if ( (p = playlistDAO.findById(pidVal).orElse(null)) != null ) {
		Tuple t = bulkFindUtil(videoIds.split(SPLIT_PARAM_REGEX));
		p.getVideos().removeAll(t.videos);
		p = playlistDAO.create(p); // SaveOrUpdate Playlist
		dat.setSuccess(p != null).setEntity(isMinimalRequest() ? null : p);
		if ( t.delta == 0) {
		    dat.setErrorMessage(String.format("%d of selected videos were not found", t.delta));
		}
	    }
	    else {
		dat.setStatus(Response.Status.NOT_FOUND).setErrorMessage(String.format("No such playlist [%s]", pidVal ));
	    }
	}
	else {
	    dat.setStatus(Response.Status.FORBIDDEN);
	}
	return doPATCH(dat).build();
    }

}

