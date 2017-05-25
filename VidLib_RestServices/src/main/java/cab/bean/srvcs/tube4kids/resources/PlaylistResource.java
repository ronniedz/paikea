package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.util.Arrays;
import java.util.Collection;
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

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.auth.AdminOrOwner;
import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.Video;
import cab.bean.srvcs.tube4kids.db.PlaylistDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;

/**
 * @author ronalddennison
 */
@Path("/playlist")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ RoleNames.GUARDIAN_ROLE, RoleNames.MEMBER_ROLE,
	RoleNames.ADMIN_ROLE, RoleNames.PLAYLIST_EDIT_ROLE,
	RoleNames.CONTENT_MODERATOR_ROLE, RoleNames.SUDO_ROLE })
public class PlaylistResource extends BaseResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistResource.class);

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
	boolean respBody = (p != null);
	ResponseData dat = new ResponseData()
		.setSuccess(respBody).setEntity(respBody ? isMinimalRequest() ? p.getId() : p : null);
	return doPOST(dat).build();
    }

    @GET
    @UnitOfWork
    public Response listPlaylists() {
	List<Playlist> list = playlistDAO.findAll();
	return doGET(new ResponseData(list).setSuccess(list != null)).build();
    }

    @UnitOfWork
    @PATCH
    @AdminOrOwner(adminRoles = { RoleNames.ADMIN_ROLE,
	    RoleNames.PLAYLIST_EDIT_ROLE, RoleNames.PLAYLIST_MANAGER_ROLE }, ownerTests = { "isMyPlaylist:arg0" })
    public Response updatePlaylist(Playlist objectData, @Auth User user) {
	final ResponseData dat = new ResponseData().setSuccess(false);
	Playlist o = null;
	if ((o = playlistDAO.update(objectData)) != null) {
	    dat.setSuccess(true).setEntity(isMinimalRequest() ? null : o);
	    dat.setLocation(UriBuilder.fromResource(getClass())
		    .path(o.getId().toString()).build());
	} else {
	    dat.setStatus(Response.Status.NOT_FOUND);
	}
	return doPATCH(dat).build();
    }

    @Path("/{id: [0-9]+}")
    @DELETE
    @UnitOfWork
    @AdminOrOwner(adminRoles = { RoleNames.ADMIN_ROLE,
	    RoleNames.PLAYLIST_EDIT_ROLE, RoleNames.PLAYLIST_MANAGER_ROLE }, ownerTests = { "isMyPlaylist:arg0" })
    public Response deletePlaylist(@PathParam("id") Long pidVal, @Auth User user) {
	Playlist o = null;
	if (user.hasAnyRole(RoleNames.PLAYLIST_MANAGER)) {
	    o = (playlistDAO.findById(pidVal).orElseThrow(() -> new javax.ws.rs.NotFoundException()));
	    o = playlistDAO.delete(o.getId());
	} else {
	    o = playlistDAO.loadUserPlaylists(user).stream()
		    .filter(plitem -> plitem.getId().equals(pidVal))
		    .findFirst() .orElseThrow(() -> new javax.ws.rs.NotFoundException());
	    o = playlistDAO.delete(o.getId());
	}
	return doDELETE(
		new ResponseData().setSuccess(o != null).setEntity(
			isMinimalRequest() ? null : o)).build();
    }

    @Path("/pick/{pid: [0-9]+}")
    @PUT
    @UnitOfWork
    public Response pickPlaylist(@PathParam("pid") Long pid,
	Playlist destPlaylist, @Auth User user) {

	// TODO In the future won't need to liberate() as only decoupled
	// playlists will be findable
	final Playlist org = liberatePlaylist(pid, user);

	if (destPlaylist.getTitle().isEmpty()) {
	    destPlaylist.setTitle(org.getTitle() + " (picked)");
	}
	destPlaylist = playlistDAO.create(destPlaylist);
	destPlaylist.setVideos(org.getVideos().stream().map(
		orgVid -> { return videoDAO.findById(orgVid.getVideoId()).get(); }
	).collect(Collectors.toSet()));

	playlistDAO.create(destPlaylist);
	return doPOST(
		new ResponseData(destPlaylist).setSuccess(destPlaylist != null)
	).build();
    }

    @Path("/liberate/{pid: [0-9]+}")
    @PUT
    @UnitOfWork
    @RolesAllowed({ RoleNames.GUARDIAN_ROLE, RoleNames.MEMBER_ROLE,
	    RoleNames.PLAYLIST_EDIT_ROLE, RoleNames.PLAYLIST_MANAGER_ROLE })
    public Playlist liberatePlaylist(@PathParam("pid") Long pid, @Auth User user) {

	final Playlist org = playlistDAO.retrieve(pid);
	Playlist o = new Playlist();

	o.setTitle(org.getTitle() + " (copy)");
	o.setUser(user);
	o = playlistDAO.create(o);

	Set<String> needles = org.getVideos().stream().map(Video::getVideoId)
		.collect(Collectors.toSet());

	o.getVideos().addAll(videoDAO.findWithIds(needles));
	return playlistDAO.create(o);
    }

    @Path("/my")
    @GET
    @UnitOfWork
    @RolesAllowed({ RoleNames.GUARDIAN_ROLE, RoleNames.MEMBER_ROLE,
	    RoleNames.PLAYLIST_EDIT_ROLE, RoleNames.PLAYLIST_MANAGER_ROLE })
    public Response listOwnPlaylists(@Auth User user) {
	return doGET(new ResponseData(playlistDAO.loadUserPlaylists(user)).setSuccess(true)).build();
    }

    @Path("/video/{pidVal: [0-9]+}")
    @PATCH
    @UnitOfWork
    @RolesAllowed({ RoleNames.GUARDIAN_ROLE, RoleNames.PLAYLIST_MANAGER_ROLE, RoleNames.MEMBER_ROLE })
    public Response addVideos(@PathParam("pidVal") Long pidVal, Set<String> videoIds, @Auth User user) {
	final ResponseData dat = new ResponseData().setSuccess(false);

	Optional<Playlist> subjectPlaylistOpt = null;

	if (user.hasAnyRole(new String[] {RoleNames.GUARDIAN_ROLE, RoleNames.MEMBER_ROLE,})) {
	    // LOAD lazy collection of playlists
	    subjectPlaylistOpt = playlistDAO.loadUserPlaylists(user).stream()
		    .filter(plitem -> plitem.getId().equals(pidVal))
		    .findFirst();

	} else if (user.hasAnyRole(RoleNames.PLAYLIST_MANAGER)) {
	    subjectPlaylistOpt = playlistDAO.findById(pidVal);
	}

	if (subjectPlaylistOpt != null) {

	    Playlist p =  subjectPlaylistOpt .orElseThrow(() -> new javax.ws.rs.NotFoundException());
	    final Pair<Integer, Collection<Video>> t = bulkFindUtil(videoIds);
	    final Set<Video> listVids = p.getVideos();
	    p.getVideos().addAll(
		    // Remove duplicates
            	    t.getRight().stream().filter(uVid -> {
            		return ! listVids.contains(uVid);
            	    }) .collect(Collectors.toSet())
	    );
//	    p.getVideos().addAll(t.getRight());
	    p = playlistDAO.create(p);
	    dat.setSuccess(true).setEntity(isMinimalRequest() ? null : p);
	    if (t.getLeft().equals(0)) {
		dat.setErrorMessage(String.format("{%d} of selected videos were not found", t.getLeft()));
	    }
	} else {
	    dat.setStatus(Response.Status.FORBIDDEN);
	}
	return doPATCH(dat).build();
    }

    private Pair<Integer, Collection<Video>> bulkFindUtil(String... videoIds) {
	return bulkFindUtil(Arrays.asList(videoIds));
    }

    private Pair<Integer, Collection<Video>> bulkFindUtil(
	    Collection<String> videoIds) {
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
    @RolesAllowed({ RoleNames.GUARDIAN_ROLE, RoleNames.PLAYLIST_EDIT_ROLE,
	    RoleNames.PLAYLIST_MANAGER_ROLE })
//    @AdminOrOwner(adminRoles={ RoleNames.GUARDIAN_ROLE, RoleNames.PLAYLIST_EDIT_ROLE,
//	RoleNames.PLAYLIST_MANAGER_ROLE },
//	ownerTests="isMyPlaylist:pidVal")
    public Response dropVideos(@PathParam("pidVal") Long pidVal, @PathParam("videoIds") String videoIds, @Auth User user) {
	ResponseData dat = new ResponseData().setSuccess(false);

	final Optional<Playlist> subjectPlaylistOpt = (user.hasRole(RoleNames.GUARDIAN_ROLE))
		? playlistDAO.loadUserPlaylists(user)
			.stream()
			.filter(plitem -> plitem.getId().equals(pidVal)).findFirst()

		: (user.hasAnyRole(RoleNames.PLAYLIST_MANAGER))
			? playlistDAO.findById(pidVal)
			// User has no access
			: null;

	if (subjectPlaylistOpt != null) {
	    Playlist subjectPlaylist = subjectPlaylistOpt.orElseThrow(() -> new javax.ws.rs.NotFoundException());
	    Pair<Integer, Collection<Video>> tuple = bulkFindUtil(videoIds.split(SPLIT_PARAM_REGEX));
	    subjectPlaylist.getVideos().removeAll(tuple.getRight());
	    subjectPlaylist = playlistDAO.create(subjectPlaylist);

	    dat.setSuccess(true).setEntity(isMinimalRequest() ? null : subjectPlaylist);

	    if (tuple.getLeft().equals(0)) {
		dat.setErrorMessage(String.format("%d of selected videos were not found", tuple.getLeft()));
	    }

	} else {
	    // With @Auth, it should never get to this
	    dat.setStatus(Response.Status.FORBIDDEN);
	}
	return doPATCH(dat).build();
    }
}
