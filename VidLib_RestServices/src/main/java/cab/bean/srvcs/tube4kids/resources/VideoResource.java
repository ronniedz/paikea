package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.RelVideo;
import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.core.VideoGenre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
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
    private final Driver driver;

    public VideoResource(VideoDAO videoDAO, GenreDAO genreDAO, UserDAO userDAO, Driver ndriver) {
	super();
	this.videoDAO = videoDAO;
	this.genreDAO = genreDAO;
	this.userDAO = userDAO;
	this.driver = ndriver;
    }

    private Session getSession() {
	return this.driver.session();
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
	String query = "MATCH (v:Video)-[:WITH]-(genre:Genre) return v;";

	// StatementResult result = session.run(query);

	List<Map<String, Object>> list = getSession().run(query).list(r -> unwrap(r, "v"));
	getSession().close();

	list.forEach(d -> System.out.println(d));


	// while (result.hasNext()) {
	// Record record = result.next();
	// org.neo4j.driver.v1.Value nv = record.get("v");
	// RelVideo vid = (RelVideo) nv.asObject();
	// System.out.println(vid.toString());
	// }
	return list;
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
	List<String>ids = new ArrayList();
	for (int i = 0; i < videos.size(); i++) {
	    videos.get(i).setUserId(1L);
	    ids.add(videoDAO.addVideoYTVideo(videos.get(i)));
	}
	URI location = UriBuilder.fromUri("/video/" +  ids.get(0) ).build();
	return Response
		.status(Response.Status.CREATED)
		.header("Location", location)
		.entity(ImmutableMap.<String, Object> builder()
			.put("id", ids)
			.put("uri", location)
			.build()
			)
			.build();
    }
    

}
