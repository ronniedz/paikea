package cab.bean.srvcs.tube4kids.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.Qwrap;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.core.MongoVideo;
import cab.bean.srvcs.tube4kids.remote.YouTubeAPIProxy;


@Path("/ytapi/v1")
@Produces(MediaType.APPLICATION_JSON)
public class YouTubeVideoResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeVideoResource.class);

    private final YouTubeAPIProxy ytProxyClient;

    public YouTubeVideoResource(final YouTubeAPIProxy ytProxyClient) {
        this.ytProxyClient = ytProxyClient;
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response search(@Context UriInfo uriInfo) {
	YouTubeResponse qwrap  = ytProxyClient.runSearchQuery(asMap(uriInfo.getQueryParameters()));
//	List<MongoVideo> mvids = qwrap.getItems();
	
	LOGGER.info("Returned from YT-queue:\n{}\n" , qwrap);
	return Response.ok(qwrap).build();
    }
    
//    private void addCallBack(MongoVideo video) {
//	video.setACallback("add to", "/video/" + video.getVideoId() + "/playlist/0")
//	.setACallback("add to", "/video/" + video.getVideoId() + "/playlist/0")
//	;
//    }
    /**
     * A helper to convert MultivaluedMap&lt;String, String[]&gt; to Map&lt;String, String&gt;.
     * <p>
     * <b>Note</b>, only the first value - i.e. the zeroth item in value array - is copied to resulting Map.
     *  </p>
     * @param queryParameters
     * @return
     */
    private Map<String, String> asMap(MultivaluedMap<String, String> queryParameters) {
	Map<String, String> temp = new HashMap<String, String>();
	for (String nm: queryParameters.keySet()) {
	    temp.put(nm, queryParameters.getFirst(nm));
	}
	return temp;
    }
}
