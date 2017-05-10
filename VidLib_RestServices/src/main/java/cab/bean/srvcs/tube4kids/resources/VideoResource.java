package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;
import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.Video;
import cab.bean.srvcs.tube4kids.core.VideoGenre;
import cab.bean.srvcs.tube4kids.db.Neo4JGraphDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.remote.YouTubeAPIProxy;
import cab.bean.srvcs.tube4kids.utils.StringTool;

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.MEMBER_ROLE}) 
public class VideoResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoResource.class);

//    private final Neo4JGraphDAO neo4jGraphDAO;
    private final VideoDAO videoDAO;
    private final YouTubeAPIProxy ytProxyClient;


    public VideoResource(VideoDAO videoDAO, Neo4JGraphDAO neo4jGraphDAO, final YouTubeAPIProxy ytProxyClient) {
	super();
	this.videoDAO = videoDAO;
        this.ytProxyClient = ytProxyClient;
//	this.neo4jGraphDAO = neo4jGraphDAO;
    }

    @GET
    @UnitOfWork
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.MEMBER_ROLE, RoleNames.CHILD_ROLE}) 
    public Response listVideos() {
	Object vList = videoDAO.findAll();
	return doGET(new ResponseData(vList).setSuccess(vList != null)).build();
    }

    @Path("/{vid}")
    @GET
    @UnitOfWork
    @PermitAll
    public Response viewVideo(@PathParam("vid") String vid) {
	Video v = videoDAO.findById(vid).orElse(null);
	return doGET(new ResponseData(v).setSuccess(v != null)).build();
    }

    @Path("/{vid}/genre")
    @PATCH
    @UnitOfWork
    @RolesAllowed({RoleNames.GUARDIAN_ROLE, RoleNames.CONTENT_MODERATOR_ROLE}) 
    public Response addGenre(@PathParam("vid") String vid, Long[] genreIds, @Auth User user) {

	ResponseData dat = new ResponseData();

	try {
	    Video video = videoDAO.findById(vid).orElse(null);

	    if (video == null) {
		dat.setStatus(Response.Status.NOT_FOUND);
	    }
	    else {
		VideoGenre vg = new VideoGenre(video, user, genreIds[0], genreIds[1]);
		int vglistIndex = video.getVideoGenres().indexOf(vg);

		if (vglistIndex > -1) {
		    video.getVideoGenres().get(vglistIndex).setGenreIds(genreIds);
		} else {
		    video.getVideoGenres().add(vg);
		}

		boolean isMini = isMinimalRequest();
		Object o = videoDAO.create(video, isMini); // This is a saveOrUpdate() call

		dat
		 .setSuccess(true)
		 .setEntity(o); 
	    }
	} catch (Exception nsee) {
	    dat
	    	.setSuccess(false).setStatus(Response.Status.BAD_REQUEST)
		.setErrorMessage(nsee.getMessage()); // TODO make safe
	}
        return doPATCH(dat).build();
    }

    @Path("/{id}")
    @DELETE
    @UnitOfWork
    @RolesAllowed({RoleNames.MEMBER_ROLE, RoleNames.CONTENT_MODERATOR_ROLE}) 
    public Response deleteVideo(@PathParam("id") String id, @Auth User user) {
	ResponseData dat = new ResponseData();
	Video video = videoDAO.delete(id);
	return doDELETE(dat.setSuccess(video != null).setEntity(isMinimalRequest() ? null : video)).build();
    }

    @POST
    @UnitOfWork
    @RolesAllowed({RoleNames.MEMBER_ROLE, RoleNames.CONTENT_MODERATOR_ROLE}) 
    public Response createVideos(List<Video> videos, @Auth User user) {

	String vids =  StringTool.joinMap(videos, ",", vidIn -> vidIn.getVideoId());
	Map<String, String> paramMap = ImmutableMap.of("part", "contentDetails,snippet", "id" ,vids);

	// Pull video detail from youtube
	YouTubeVideoDetailResponse detailResp = ytProxyClient.runVideoDetail(paramMap);
	List<String>ids = new ArrayList<String>();

	for (int syncedIndex = 0; syncedIndex <  videos.size(); syncedIndex++ ) {
	    Video tempVideo = videos.get(syncedIndex);
	    tempVideo.setUserId(user.getId());
	    tempVideo.setUser(user);
	    tempVideo.getVideoGenres().forEach( g -> {
		g.getPk().setUser(user);
		g.getPk().setVideo(tempVideo);
	    });

	    tempVideo.setDetail(detailResp.getItems().get(syncedIndex));
	    ids.add(videoDAO.create(tempVideo).getVideoId());
//	    neo4jGraphDAO.insert(video);
	}
        return doPOST(new ResponseData(ids).setSuccess(true)).build();
    }
}

