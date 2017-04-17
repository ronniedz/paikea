package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.Role;
import cab.bean.srvcs.tube4kids.core.Role.Names;
import cab.bean.srvcs.tube4kids.core.Video;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.VideoGenre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import cab.bean.srvcs.tube4kids.db.Neo4JGraphDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import cab.bean.srvcs.tube4kids.remote.YouTubeAPIProxy;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;
import cab.bean.srvcs.tube4kids.utils.StringTool;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.LongParam;

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
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.helpers.collection.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({Role.Names.ADMIN_ROLE, Role.Names.MEMBER_ROLE}) 
public class VideoResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoResource.class);

    private final VideoDAO videoDAO;
//    private final Neo4JGraphDAO neo4jGraphDAO;
    private final YouTubeAPIProxy ytProxyClient;


    public VideoResource(VideoDAO videoDAO, GenreDAO genreDAO, UserDAO userDAO, Neo4JGraphDAO neo4jGraphDAO, final YouTubeAPIProxy ytProxyClient) {
	super();
	this.videoDAO = videoDAO;
//	this.neo4jGraphDAO = neo4jGraphDAO;
        this.ytProxyClient = ytProxyClient;
    }

    @GET
    @UnitOfWork
    @RolesAllowed({Names.ADMIN_ROLE, Names.MEMBER_ROLE, Names.CHILD_ROLE}) 
    public Response listVideos() {
	Object vList = videoDAO.findAll();
	return doGET(new ResponseData(vList).setSuccess(vList != null)).build();
    }
    
//    @GET
//    @Path("shiz")
//    @UnitOfWork
//    public List<Map<String, Object>> listneo() {
//	Session session = getSession();
//	String query = "MATCH (v:Video)-[:WITH]-(genre:Genre) return v;";
//
//	// StatementResult result = session.run(query);
//	
//	List<Map<String, Object>> list = session.run(query).list(r -> unwrap(r, "v"));
//	session.close();

//	list.forEach(d -> System.out.println(d));


	// while (result.hasNext()) {
	// Record record = result.next();
	// org.neo4j.driver.v1.Value nv = record.get("v");
	// Video vid = (Video) nv.asObject();
	// System.out.println(vid.toString());
	// }
//	return neo4jGraphDAO.listAllVideo();
//    }

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
    @RolesAllowed({Role.Names.GUARDIAN_ROLE, Role.Names.CONTENT_MODERATOR_ROLE}) 
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
    @RolesAllowed({Role.Names.MEMBER_ROLE, Role.Names.CONTENT_MODERATOR_ROLE}) 
    public Response deleteVideo(@PathParam("id") String id, @Auth User user) {
	ResponseData dat = new ResponseData();
	Video video = videoDAO.delete(id);
	return doDELETE(dat.setSuccess(video != null).setEntity(isMinimalRequest() ? null : video)).build();
    }

    @POST
    @UnitOfWork
    @RolesAllowed({Role.Names.MEMBER_ROLE, Role.Names.CONTENT_MODERATOR_ROLE}) 
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
