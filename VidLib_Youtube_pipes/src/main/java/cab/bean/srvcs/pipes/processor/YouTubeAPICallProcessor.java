package cab.bean.srvcs.pipes.processor;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;
import cab.bean.srvcs.tube4kids.resources.YouTubeAgent;

public class YouTubeAPICallProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAPICallProcessor.class);
    private final YouTubeAgent youTubeAgent;
    
    
    public YouTubeAPICallProcessor(YouTubeAgent youTubeAgent) {
	super();
	this.youTubeAgent = youTubeAgent;
    }
    
    public YouTubeAgent getYouTubeAgent() {
        return youTubeAgent;
    }

    public void process(Exchange exchange) throws Exception {

	String path = exchange.getFromRouteId();
//	String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
	LOGGER.debug("path: {}" , path);
	
	if (path.equals("detail")) {

	    Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);
	    
	    Map<String, String> params = request.getResourceRef().getQueryAsForm().getValuesMap();

	    YouTubeVideoDetailResponse details = null;
	    Response reply = youTubeAgent.doRequest(params, path);
	    if (Response.Status.OK.getStatusCode() == reply.getStatusInfo().getStatusCode()) {
		details =  reply.readEntity(YouTubeVideoDetailResponse.class);
	    }
	    exchange.getOut().setBody(details);
	}
	else {
		Map<String,String> params = exchange.getIn().getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, Map.class);
		YouTubeResponse ytResp= null;

		// TODO the value should be set in request or intermediate client.
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
}
