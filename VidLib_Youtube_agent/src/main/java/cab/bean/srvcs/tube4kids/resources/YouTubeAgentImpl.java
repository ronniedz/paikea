package cab.bean.srvcs.tube4kids.resources;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
//import cab.bean.srvcs.tube4kids.resources.Config;

import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * A YouTube API client.
 */
public class YouTubeAgentImpl implements YouTubeAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAgentImpl.class);
    private final Client jClient;
    private final WebTarget baseTarget;
    private final String apiKey;
    private final String nullStr = null;
    
    
    private final String videoSearchPath;

    private final String videoDetailPath;

    public YouTubeAgentImpl(Map<String, String> props) {
	this.jClient = ClientBuilder.newClient( new ClientConfig().register(LOGGER).register(new JacksonJaxbJsonProvider() )); 
	this.videoSearchPath = props.get("videoSearchPath");
	this.videoDetailPath = props.get("videoDetailPath");
	this.apiKey = props.get("apiKey");
	this.baseTarget = jClient.target(props.get("host")).path(props.get("contextPath"));
    }
    
    public WebTarget setApiKey(String apiKey) {
	return baseTarget.queryParam("key", nullStr).queryParam("key", apiKey);
    }
    
    public Response doRequest(Map<String, String> params, String route) {
	Response resp = null;
	switch (route) {
	    case "detail" :
		resp = runVideoDetailsQuery(params);
		break;
	    default :
		resp = runSearchQuery(params);
		break;
	}
	return resp;
    }


    public Response runSearchQuery(Map<String, String> params)  {
	WebTarget webResource = Config.setRequestQueryParams(baseTarget.path(videoSearchPath), params);
	if ( ! params.containsKey("key")) {
	    webResource = webResource.queryParam("key", apiKey);
	}
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeResponse body = remoteReply.readEntity(YouTubeResponse.class);
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }
    
    public Response runVideoDetailsQuery(Map<String, String> params)  {
	WebTarget webResource = Config.setRequestQueryParams(baseTarget.path(videoDetailPath), params);
	if ( ! params.containsKey("key")) {
	    webResource = webResource.queryParam("key", apiKey);
	}

	
	LOGGER.debug("YT Details Request:\n{}\n", webResource.getUri().toString());
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeVideoDetailResponse body = remoteReply.readEntity(YouTubeVideoDetailResponse.class);
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }

}
