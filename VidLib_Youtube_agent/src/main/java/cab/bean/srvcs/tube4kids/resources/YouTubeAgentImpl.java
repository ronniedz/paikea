package cab.bean.srvcs.tube4kids.resources;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.pipes.route.YoutubeResourceConfiguration;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
//import cab.bean.srvcs.tube4kids.resources.Config;

import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * A YouTube API client.
 */
public class YouTubeAgentImpl implements YouTubeAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAgentImpl.class);
    private final Client jClient;
    private  WebTarget baseTarget;
    private final String nullStr = null;
    
    public YouTubeAgentImpl(Configuration.YoutubeResourceConfiguration configuration) {
	
    }
    
    public YouTubeAgentImpl() {
	this.jClient = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()); //.register(mapper); 
	this.baseTarget = jClient
		.target( Config.PropKey.DATASRC_DOMAIN.getValue() + Config.PropKey.DATASRC_CTX_URI.getValue() )
		.queryParam("key", nullStr)
		.queryParam("key", Config.PropKey.APIKEY.getValue());
    } 

    public YouTubeAgentImpl(JacksonJsonProvider jsonProvider) {
	this.jClient = ClientBuilder.newClient().register(jsonProvider); 
	this.baseTarget = jClient
		.target( Config.PropKey.DATASRC_DOMAIN.getValue() + Config.PropKey.DATASRC_CTX_URI.getValue() )
		.queryParam("key", nullStr)
		.queryParam("key", Config.PropKey.APIKEY.getValue());
    }

    public YouTubeAgentImpl(Client jClient) {
	this.jClient = jClient; 
	this.baseTarget = jClient
		.target( Config.PropKey.DATASRC_DOMAIN.getValue() + Config.PropKey.DATASRC_CTX_URI.getValue() )
		.queryParam("key", nullStr)
		.queryParam("key", Config.PropKey.APIKEY.getValue());
    }
    
    
    public YouTubeAgentImpl(String apiKey) {
	this();
	setApiKey(apiKey);
    }
    
    public WebTarget setApiKey(String apiKey) {
	return baseTarget = baseTarget.queryParam("key", nullStr).queryParam("key", apiKey);
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
	WebTarget webResource = baseTarget.path(Config.PropKey.DATASRC_SEARCH_SRV_URI.getValue());
	webResource = Config.setRequestQueryParams(webResource, params);
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeResponse body = remoteReply.readEntity(YouTubeResponse.class);
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }
    
    public Response runVideoDetailsQuery(Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(Config.PropKey.DATASRC_DETAILS_SRV_URI.getValue());
	webResource = Config.setRequestQueryParams(webResource, params);
	LOGGER.debug("YT Details Request:\n{}\n", webResource.getUri().toString());
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeVideoDetailResponse body = remoteReply.readEntity(YouTubeVideoDetailResponse.class);
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }

}
