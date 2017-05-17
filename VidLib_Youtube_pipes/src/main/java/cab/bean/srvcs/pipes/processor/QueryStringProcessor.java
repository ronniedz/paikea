package cab.bean.srvcs.pipes.processor;

import java.util.Map;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongojack.JacksonDBCollection;
import org.restlet.Request;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.QueueException;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class QueryStringProcessor implements Processor {
    private static Log LOGGER = LogFactory.getLog(QueryStringProcessor.class);

    private final DB db;

    public QueryStringProcessor(DB db) {
	this.db = db;
    }

    @Override
    public void process(Exchange exchange) throws QueueException {
	LOGGER.debug("IN  - GET Headers(): " + exchange.getIn().getHeaders().keySet());
	LOGGER.debug("OUT - GET Headers(): " + exchange.getOut().getHeaders().keySet());

	final Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);
	final Map<String, String> params = request.getResourceRef().getQueryAsForm().getValuesMap();
	
	try {
    	    final VideoSearchRequest aQuery = new VideoSearchRequest();
    	    BeanUtils.copyProperties(aQuery, params);
    	    
    	    final String collectionName = PersistenceHelper.makeNameFromQuery(params);
    	    final Optional<VideoSearchRequest> needleState =  cacheContains(aQuery, collectionName);
    	    
    	    exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, needleState.orElse(aQuery) );
    	    exchange.getOut().setHeader(PersistenceHelper.HDR_FOUNDQUERY, needleState.isPresent());
    	    //	    exchange.getIn().setHeader(PersistenceHelper.HDR_FOUNDQUERY, Boolean.TRUE);
    	    // The Java DSL route needs me to set the 'found' flag in the IN-message 
    	    exchange.getOut().setBody(aQuery);
    	    exchange.getOut().setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, params);
    	    exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME,collectionName);
	}
	catch (Exception e) {
	    throw new QueueException(e.getMessage());
	}
    }

    private Optional<VideoSearchRequest> cacheContains(VideoSearchRequest queryObj, String collectionName) {
        VideoSearchRequest foundSearchResponse = null;

	if ( db.collectionExists(collectionName) ) {
	    JacksonDBCollection<VideoSearchRequest, org.bson.types.ObjectId> metadat = JacksonDBCollection.wrap(
		    db.getCollection(PersistenceHelper.META_DATA_DB_NAME),
		    VideoSearchRequest.class,
		    org.bson.types.ObjectId.class
	    );
            DBObject query = new BasicDBObject();
            query.put("pageToken" , Optional.ofNullable(queryObj.getPageToken()).orElse(""));
            query.put("collectionName" , collectionName);
            foundSearchResponse = metadat.findOne(query);
	}
	return Optional.ofNullable(foundSearchResponse);
    }

}
