package cab.bean.srvcs.pipes.processor;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;
import cab.bean.srvcs.tube4kids.resources.YouTubeAgent;

public class Http4Processor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http4Processor.class);
    private final ObjectMapper objectMapper;
    
    
    public Http4Processor(ObjectMapper objectMapper) {
	super();
	this.objectMapper = objectMapper;
    }
   
/**
 * Camel will store the HTTP response from the external server on the OUT body. All headers from the IN message will be copied 
 * to the OUT message, so headers are preserved during routing. Additionally Camel will add the HTTP response headers as well 
 * to the OUT message headers.
 */
    public void process(Exchange exchange) throws Exception {

	org.apache.camel.Message messageIn = exchange.getIn();
	org.apache.camel.Message message = exchange.getOut();
	
	YouTubeResponse ytResp = objectMapper.readValue((InputStream)messageIn.getBody(), YouTubeResponse.class);
//	VideoSearchRequest ytReq = objectMapper.readValue((InputStream)message.getBody(), VideoSearchRequest.class);
	VideoSearchRequest ytReq = messageIn.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, VideoSearchRequest.class);
 //   	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, exchange.getIn().getBody(VideoSearchRequest.class));
	
	message.setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, messageIn.getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME));
	message.setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, messageIn.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA));
	message.setHeader(PersistenceHelper.HDR_FOUNDQUERY, messageIn.getHeader(PersistenceHelper.HDR_FOUNDQUERY));
        
	String collectionName = messageIn.getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
    	System.err.println("PersistenceHelper.HDR_NAME_COLLECTION_NAME: " + collectionName);
	ytResp.setCollectionName(collectionName);
	message.setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, collectionName);
	
	
        System.err.println("IN  - GET Headers(): " + exchange.getIn().getHeaders().keySet());
        System.err.println("OUT - GET Headers(): " + exchange.getOut().getHeaders().keySet());

	System.err.println("ytResp: " + ytResp);
	
	message.setBody(ytResp);
	

//
////		Map<String,String> params = exchange.getIn().getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, Map.class);
//		YouTubeResponse ytResp  = exchange.getIn().getBody(YouTubeResponse.class);
//
//		// TODO the value should be set in request or intermediate client.
//            	String collectionName = exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
//            	LOGGER.debug("collectionName: ", collectionName);
//            	ytResp.setCollectionName(collectionName);
//            
//            	
//            	// Copy into out message for wireTap
//            	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME,collectionName);
//            	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, exchange.getIn().getBody(VideoSearchRequest.class));
//            	exchange.getOut().setBody(ytResp);
    }



//	Map<String,String> params = exchange.getIn().getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, Map.class);
//	YouTubeResponse ytResp= null;
//
//	// TODO the value should be set in request or intermediate client.
//    	params.put("fields", "etag,regionCode,items,kind,nextPageToken,prevPageToken,pageInfo");
//    	String collectionName = exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
//    
//    	Response reply = youTubeAgent.runSearchQuery( params );
//    
//    	if (Response.Status.OK.getStatusCode() == reply.getStatusInfo().getStatusCode()) {
//    	    ytResp =  reply.readEntity(YouTubeResponse.class);;
//    	}
//    	
//    	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME,collectionName);
//    	ytResp.setCollectionName(collectionName);
//    	
//    	// Copy into out message for wireTap
//    	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, exchange.getIn().getBody(VideoSearchRequest.class));
//    	exchange.getOut().setBody(ytResp);
//
//	    }



}
