package cab.bean.srvcs.pipes.processor;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.resources.YouTubeAgent;

public class YouTubeAPICallProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAPICallProcessor.class);

    private YouTubeAgent youTubeAgent = null;
    
    public YouTubeAgent getYouTubeAgent() {
        return youTubeAgent;
    }

    public void setYouTubeAgent(YouTubeAgent yta) {
        this.youTubeAgent = yta;
    }
    
    public void process(Exchange exchange) throws Exception {

	YouTubeResponse ytResp= null;
	Map<String,String> params = exchange.getIn().getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, Map.class);
	
	// TODO the value should be set by sender.
	params.put("fields", "etag,regionCode,items,kind,nextPageToken,prevPageToken,pageInfo");
	String collectionName = exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);

	Response reply = youTubeAgent.runSearchQuery( params );

	if (Response.Status.OK.getStatusCode() == reply.getStatusInfo().getStatusCode()) {
	    ytResp =  reply.readEntity(YouTubeResponse.class);;
	}
	
	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME,collectionName);
	ytResp.setCollectionName(collectionName);
	
	// Copy into out message for wireTap
	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, exchange.getIn().getBody(VideoSearchRequest.class));
	exchange.getOut().setBody(ytResp);
    }
}
