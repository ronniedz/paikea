package cab.bean.srvcs.tube4kids.resources;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.core.MongoVideo;
import cab.bean.srvcs.tube4kids.core.MongoVideo;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * The web client that communicates directly with YouTube API
 */
public class YouTubeAgentImpl implements YouTubeAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAgentImpl.class);

    private Client jClient = null;
    private WebTarget baseTarget = null;
   
    public YouTubeAgentImpl() {
	this.jClient = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider());	//.register(mapper); 
	this.baseTarget = jClient.target(Config.DATASRC_DOMAIN + Config.DATASRC_CTX_URL );
    } 

    public YouTubeAgentImpl(String apiKey) {
	this();
	setApiKey(apiKey);
    }

    public String runSearch(Map<String, String> params)  {

	WebTarget webResource = baseTarget.path(Config.DATASRC_SRV_URL);
	webResource = Config.setRequestQueryParams(webResource, params);
	
	Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get(Response.class);
	String body = "{'msg': '" + clientResponse.getStatusInfo().getReasonPhrase() + "'}";
	
	if (Response.Status.OK.getStatusCode() == clientResponse.getStatus()) {
	    body = clientResponse.readEntity(String.class);
	}
	return body;
    }
    
    public Response runSearchQuery(Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(Config.DATASRC_SRV_URL);
	webResource = Config.setRequestQueryParams(webResource, params);
	
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	
	YouTubeResponse body = remoteReply.readEntity(YouTubeResponse.class);
	LOGGER.debug("Response.entity(body)\n {}\n" , body);
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }

    
    public List<MongoVideo> runSearchList(Map<String, String> params)  {
	
	WebTarget webResource = baseTarget.path(Config.DATASRC_SRV_URL);
	webResource = Config.setRequestQueryParams(webResource, params);
	
	Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get(Response.class);

	MongoVideo tempVideo = new MongoVideo(clientResponse.getStatusInfo().getReasonPhrase());
	
	List listOfVideos = Arrays.asList(new MongoVideo[] { tempVideo });
	if (Response.Status.OK.getStatusCode() == clientResponse.getStatus()) {
	    YouTubeResponse tq  =  clientResponse.readEntity(YouTubeResponse.class);
	    listOfVideos = tq.getItems();
	}
	
	return (List<MongoVideo>) listOfVideos;
    }

    public void setApiKey(String apiKey) {
	this.baseTarget = baseTarget.queryParam("key", apiKey);
    }

}
