package cab.bean.srvcs.tube4kids.resources;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
//import cab.bean.srvcs.tube4kids.resources.Config;

import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * A YouTube API client.
 */
public class YouTubeAgentImpl implements YouTubeAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAgentImpl.class);
    private Client jClient = null;
    private WebTarget baseTarget = null;
    private final String nullStr = null;
    
    public YouTubeAgentImpl() {
	this.jClient = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()); //.register(mapper); 
	this.baseTarget = jClient
		.target( Config.PropKey.DATASRC_DOMAIN.getValue() + Config.PropKey.DATASRC_CTX_URI.getValue() )
		.queryParam("key", nullStr)
		.queryParam("key", Config.PropKey.APIKEY.getValue());
	
	Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
	WebTarget webTarget = client.target("http://localhost:8080/JerseyDemos/rest").path("employees").path("1");
	 
	webTarget.getEntity();
	Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_XML);
	Response response = invocationBuilder.get();
	 
//	Employee employee = response.readEntity(Employee.class);
	     
	System.out.println(response.getStatus());
	System.out.println(employee);
    } 

    public YouTubeAgentImpl(String apiKey) {
	this();
	setApiKey(apiKey);
    }

    public void setApiKey(String apiKey) {
	this.baseTarget = baseTarget.queryParam("key", nullStr).queryParam("key", apiKey);
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
	
	LOGGER.debug("YTRequest:\n{}\n", webResource.getUri().toString());
	
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeResponse body = remoteReply.readEntity(YouTubeResponse.class);
	
	LOGGER.debug("YTResponse:\n{}\n", body.toString());
	
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }
    
    public Response runVideoDetailsQuery(Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(Config.PropKey.DATASRC_DETAILS_SRV_URI.getValue());
	webResource = Config.setRequestQueryParams(webResource, params);
	
	LOGGER.debug("YT Details Request:\n{}\n", webResource.getUri().toString());
	
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeVideoDetailResponse body = remoteReply.readEntity(YouTubeVideoDetailResponse.class);
	
	LOGGER.debug("YT Details Response:\n{}\n", body.toString());
	
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }

}
