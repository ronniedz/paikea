package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.RelVideo;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.VideoGenre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import cab.bean.srvcs.tube4kids.db.Neo4JGraphDAO;
import cab.bean.srvcs.tube4kids.db.UserDAO;
import cab.bean.srvcs.tube4kids.db.VideoDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.LongParam;

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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResource {

    private final VideoDAO videoDAO;
    private final GenreDAO genreDAO;
    private final UserDAO userDAO;
    private final Neo4JGraphDAO neo4jGraphDAO;

    public VideoResource(VideoDAO videoDAO, GenreDAO genreDAO, UserDAO userDAO, Neo4JGraphDAO neo4jGraphDAO) {
	super();
	this.videoDAO = videoDAO;
	this.genreDAO = genreDAO;
	this.userDAO = userDAO;
	this.neo4jGraphDAO = neo4jGraphDAO;
    }

//    
//    @POST
//    @UnitOfWork
//    public RelVideo createVideo(RelVideo video) {
//	return videoDAO.create(video);
//    }

    @GET
    @UnitOfWork
    public List<RelVideo> listVideos() {
	return videoDAO.findAll();
    }

    private Map<String, Object> unwrap(Record r, String unkey) {
	return r.asMap(x -> x.asMap()).get(unkey);
    }
    
    @GET
    @Path("shiz")
    @UnitOfWork
    public List<Map<String, Object>> listneo() {
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
	// RelVideo vid = (RelVideo) nv.asObject();
	// System.out.println(vid.toString());
	// }
	return neo4jGraphDAO.listAllVideo();
    }

    @Path("/{vid}")
    @GET
    @UnitOfWork
    public Optional<RelVideo> viewVideo(@PathParam("vid") String vid) {
	return videoDAO.findById(vid);
    }
//    
//    @Path("/{vid}")
//    @PATCH
//    @UnitOfWork
//    public Optional<RelVideo> viewVideo(@PathParam("vid") String vid) {
//	return videoDAO.findById(vid);
//    }

//    @Path("/add/{cn}/{vids}")
//    @GET
//    @UnitOfWork
//    public List<RelVideo> addVideos(@PathParam("cn") String collectionName, @PathParam("vids") String... vids) {
//	
//	return videoDAO.findByIds(vids);
//    }
    
//    @Path("/add")
//    @POST
//    @UnitOfWork
//    public Response addYTVideo(RelVideo video) {
//	video.setUserId(1L);
//	
//	String vid = videoDAO.addVideoYTVideo(video);
//	URI location = UriBuilder.fromUri("/video/" + vid).build();
//	return Response
//		.status(Response.Status.CREATED)
//		.header("Location", location)
//		.entity(ImmutableMap.<String, Object> builder()
//            		.put("id", vid)
//            		.put("uri", location)
//            		.build()
//            	 )
//	.build();
//    }
//    
    @Path("/{vid}")
    @PATCH
    @UnitOfWork
    public Response genreize(@PathParam("vid") String vid, Long[] genreIds) {
	User user = userDAO.findById(1L).get();
	RelVideo video = videoDAO.findById(vid).get();
	VideoGenre vg = new VideoGenre(video, user, genreIds[0], genreIds[1]);
	video.getVideoGenres().add(vg);
	
	videoDAO.create(video);

	URI location = UriBuilder.fromUri("/video/" +  vid ).build();
	return Response
		.status(Response.Status.CREATED)
		.header("Location", location)
		.entity(ImmutableMap.<String, Object> builder()
			.put("uri", location)
			.build()
		)
	.build();
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deleteRelVideo(@PathParam("id") String id) {
    	videoDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @UnitOfWork
    public Response addYTVideos(List<RelVideo> videos) {
	User user = userDAO.findById(1L).get();

	List<String>ids = new ArrayList<String>();
	videos.forEach( video -> {
	    video.setUserId(1L);
	    video.setUser(user);
	    video.getVideoGenres().forEach( g -> {
		g.getPk().setUser(user);
		g.getPk().setVideo(video);
	    });
	    ids.add(videoDAO.addVideoYTVideo(video));
	    neo4jGraphDAO.insert(video);
	});
	URI location = UriBuilder.fromUri("/video").build();
	return Response
		.status(Response.Status.CREATED)
		.header("Location", location)
		.entity(ImmutableMap.<String, Object> builder()
			.put("id", ids)
			.put("uri", location)
			.build()
		).build();
    }
    

}
