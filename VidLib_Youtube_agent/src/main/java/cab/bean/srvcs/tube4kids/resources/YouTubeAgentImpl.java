package cab.bean.srvcs.tube4kids.resources;

import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * A YouTube API client.
 */
public class YouTubeAgentImpl implements YouTubeAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAgentImpl.class);
    private Client jClient = null;
    private WebTarget baseTarget = null;
    private Config cnf;
    
    public YouTubeAgentImpl() {
	cnf = new Config();
	this.jClient = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()); //.register(mapper); 
	this.baseTarget = jClient.target(cnf.DATASRC_DOMAIN + cnf.DATASRC_CTX_URL );
    } 

    public YouTubeAgentImpl(String apiKey) {
	this();
	setApiKey(apiKey);
    }

    public void setApiKey(String apiKey) {
	this.baseTarget = baseTarget.queryParam("key", apiKey);
    }
    
    public Response runSearchQuery(Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(cnf.DATASRC_SRV_URL);
	webResource = cnf.setRequestQueryParams(webResource, params);
	Response remoteReply = webResource.request(MediaType.APPLICATION_JSON).get();
	YouTubeResponse body = remoteReply.readEntity(YouTubeResponse.class);
	LOGGER.debug("Response\n\tetag: {}\n\tsize: {}\n", body.getEtag(), body.getTotalResults());
	return Response.status(remoteReply.getStatusInfo()).entity(body).build();
    }

}
