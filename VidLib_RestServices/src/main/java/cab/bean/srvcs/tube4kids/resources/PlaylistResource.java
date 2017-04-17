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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

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

/**
 * @author ronalddennison
 *
 */
@Path("/playlist")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({Names.GUARDIAN_ROLE, Names.MEMBER_ROLE, Names.ADMIN_ROLE, Names.CONTENT_MODERATOR_ROLE,
    Names.SUDO_ROLE}) 
public class PlaylistResource extends BaseResource {

    private final PlaylistDAO playlistDAO;
    private final VideoDAO videoDAO;
    
    public PlaylistResource(PlaylistDAO playlistDAO, VideoDAO videoDAO) {
	this.playlistDAO = playlistDAO;
	this.videoDAO = videoDAO;
    }

    @POST
    @UnitOfWork
    public Response createPlaylist(Playlist playlist, @Auth User user) { 
	
	playlist.setUser(user);

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

    @RolesAllowed({Names.GUARDIAN_ROLE, Names.MEMBER_ROLE, Names.PLAYLIST_MANAGER_ROLE}) 
    @PATCH
    @UnitOfWork
    public Response updatePlaylist(Playlist objectData, @Auth User user) {
	
	ResponseData dat = new ResponseData().setSuccess(false);

	Optional<Playlist> subjectPlaylistOpt = null;
	
	if (  user.hasRole(Names.GUARDIAN_ROLE) ) {
	    // LOAD lazy collection of playlists
	    subjectPlaylistOpt = playlistDAO.loadUserPlaylists(user).stream().filter(plitem -> plitem.getId().equals(objectData.getId())).findFirst();
	}
	else if ( user.hasAnyRole(Names.PLAYLIST_MANAGER )) {
	    subjectPlaylistOpt = playlistDAO.findById(objectData.getId());
	}

	if (subjectPlaylistOpt != null  ) {
	    if (subjectPlaylistOpt.isPresent()) {
		Playlist o = playlistDAO.update(subjectPlaylistOpt.get());
		dat.setSuccess(true).setEntity(isMinimalRequest() ? null : o );
		dat.setLocation( UriBuilder.fromResource(this.getClass()).path(o.getId().toString()).build() );
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
  

  @Path("/{id: [0-9]+}")
  @DELETE
  @UnitOfWork
  public Response deletePlaylist(@PathParam("id") Long pidVal, @Auth User user) {
	
	ResponseData dat = new ResponseData().setSuccess(false);
	Optional<Playlist> subjectPlaylistOpt = null;
	
	if (  user.hasRole(Names.GUARDIAN_ROLE) ) {
	    // LOAD lazy collection of playlists
	    subjectPlaylistOpt = playlistDAO.loadUserPlaylists(user).stream().filter(plitem -> plitem.getId().equals(pidVal)).findFirst();
	}
	else if ( user.hasAnyRole(Names.PLAYLIST_MANAGER )) {
	    subjectPlaylistOpt = playlistDAO.findById(pidVal);
	}

	if (subjectPlaylistOpt != null  ) {
	    if (subjectPlaylistOpt.isPresent()) {
		Playlist o = playlistDAO.delete(pidVal);
		dat.setSuccess(o != null)
		.setEntity(isMinimalRequest() ? null : o );
	    }
	}
	return doDELETE(dat).build();
  }
    
    @Path("/pick/{pid: [0-9]+}")
    @PUT
    @UnitOfWork
    public Response pickPlaylist(@PathParam("pid") Long pid, Playlist destPlaylist, @Auth User user) {
	// TODO In the future won't need to liberate() as only decoupled playlists will be findable 
	Playlist org = liberatePlaylist(pid, user);
	if ( destPlaylist.getTitle().isEmpty()) {
	    destPlaylist.setTitle(org.getTitle() + " (picked)");
	}
//	destPlaylist.setUser(user);
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
    @RolesAllowed({Names.GUARDIAN_ROLE, Names.MEMBER_ROLE, Names.PLAYLIST_MANAGER_ROLE}) 
    public Playlist liberatePlaylist(@PathParam("pid") Long pid, @Auth User user) {
	
	Playlist org = playlistDAO.retrieve(pid);
	
	Playlist o = new Playlist();

	o.setTitle(org.getTitle() + " (copy)");
	o.setUser(user);
	o = playlistDAO.create(o);

	Set<String>  needles = org.getVideos().stream().map(Video::getVideoId).collect(Collectors.toSet()); 
		
	o.getVideos().addAll(videoDAO.findWithIds(needles));
//	o.setVideos(org.getVideos().stream().map(orgVid -> { 
//	    return videoDAO.findById(orgVid.getVideoId()).get();
//	}).collect(Collectors.toSet()));
	
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


//    @Path("/user/{userId: [0-9]+}")
//    @GET
//    @UnitOfWork
//    @RolesAllowed({Names.GUARDIAN_ROLE, Names.MEMBER_ROLE, Names.PLAYLIST_MANAGER_ROLE}) 
//    public Response listUserPlaylists(@PathParam("userId") LongParam userId, @Auth User user) {
//	return doGET(new ResponseData( playlistDAO.findUserLists(user)).setSuccess(true)).build();
//    }

    
    @Path("/my")
    @GET
    @UnitOfWork
    @RolesAllowed({Names.GUARDIAN_ROLE, Names.MEMBER_ROLE, Names.PLAYLIST_MANAGER_ROLE}) 
    public Response listOwnPlaylists(@Auth User user) {
	return doGET(new ResponseData( playlistDAO.loadUserPlaylists(user)).setSuccess(true)).build();
    }
    
    @Path("/video/{pidVal: [0-9]+}")
    @PATCH
    @UnitOfWork
    @RolesAllowed({Names.GUARDIAN_ROLE, Names.PLAYLIST_MANAGER_ROLE})
    public Response addVideos(@PathParam("pidVal") Long pidVal, Set<String> videoIds, @Auth User user) {
	
	ResponseData dat = new ResponseData().setSuccess(false);

	Optional<Playlist> subjectPlaylistOpt = null;
	
	if (  user.hasRole(Names.GUARDIAN_ROLE) ) {
	    
	    // LOAD lazy collection of playlists
	    subjectPlaylistOpt = playlistDAO.loadUserPlaylists(user).stream().filter(plitem -> plitem.getId().equals(pidVal)).findFirst();
	    
	}
	else if ( user.hasAnyRole(Names.PLAYLIST_MANAGER )) {
	    subjectPlaylistOpt = playlistDAO.findById(pidVal);
	}

	if (subjectPlaylistOpt != null  ) {

	    if (subjectPlaylistOpt.isPresent()) {
		Playlist p = subjectPlaylistOpt.get();
		Pair<Integer, Collection<Video>> t = bulkFindUtil(videoIds);
		p.getVideos().addAll(t.getRight());
		p = playlistDAO.create(p); // SaveOrUpdate Playlist
		dat.setSuccess(p != null).setEntity(isMinimalRequest() ? null : p);
		if ( t.getLeft().equals(0) ) {
		    dat.setErrorMessage(String.format("{%d} of selected videos were not found", t.getLeft()));
		}
	    }
	    else {
		dat.setStatus(Response.Status.NOT_FOUND).setErrorMessage("No such playlist");
	    }
	} else {
	    dat.setStatus(Response.Status.FORBIDDEN);
	}
        return doPATCH(dat).build();
    }

    private Pair<Integer, Collection<Video>> bulkFindUtil(String... videoIds){
	return bulkFindUtil(Arrays.asList(videoIds));
    }
    
    private Pair<Integer, Collection<Video>> bulkFindUtil(Collection<String> videoIds){
	List<Video> foundVids = videoDAO.findWithIds(videoIds);
	return Pair.of(videoIds.size() - foundVids.size(), foundVids);
    }
    
    /**
     * Remove videos from playlist.
     * 
     * Video ids may be single or a list of ids. Video ids may be separated by:
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

	Optional<Playlist> subjectPlaylistOpt = null;
	
	if (  user.hasRole(Names.GUARDIAN_ROLE) ) {
	    
	    // LOAD lazy collection of playlists
	    subjectPlaylistOpt = playlistDAO.loadUserPlaylists(user).stream().filter(plitem -> plitem.getId().equals(pidVal)).findFirst();
	    
	}
	else if ( user.hasAnyRole(Names.PLAYLIST_MANAGER )) {
	    subjectPlaylistOpt = playlistDAO.findById(pidVal);
	}

	// User doesn't have access
	if (subjectPlaylistOpt != null  ) {

	    // ID might be incorrect
	    if (subjectPlaylistOpt.isPresent()) {
		
		Playlist subjectPlaylist = subjectPlaylistOpt.get();
		Pair<Integer, Collection<Video>> tuple = bulkFindUtil(videoIds.split(SPLIT_PARAM_REGEX));
		subjectPlaylist.getVideos().removeAll(tuple.getRight());
		subjectPlaylist = playlistDAO.create(subjectPlaylist); // SaveOrUpdate Playlist
		dat.setSuccess(subjectPlaylist != null).setEntity(isMinimalRequest() ? null : subjectPlaylist);
		if ( tuple.getLeft().equals(0) ) {
		    dat.setErrorMessage(String.format("%d of selected videos were not found", tuple.getLeft()));
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

