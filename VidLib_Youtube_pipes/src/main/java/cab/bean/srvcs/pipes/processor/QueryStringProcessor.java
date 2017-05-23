package cab.bean.srvcs.pipes.processor;

import java.util.Map;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.commons.beanutils.BeanUtils;
import org.mongojack.JacksonDBCollection;
import org.restlet.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.QueueException;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

/**
 * Initialize with a MongoDB connection.
 *<p /> 
 * This class expects a {@link org.restlet.Request}  on the {@code IN} Header({@link RestletConstants.RESTLET_REQUEST})
 * <blockquote>
 * The Request is processed and the following values are set on the {@code OUT} headers:
 * <ul>
 * <li>{@code 'Q'}	<b> : The original query string</b></li>
 * <li>{@code 'HDR_QUERYPARAMS_NAME'}	<b> : The query as a Map</b></li>
 * <li>{@code 'HDR_NAME_SERVICE_DEST_DATA'}	<b> : The VideoSearchRequest object</b></li>
 * <li>{@code 'HDR_NAME_COLLECTION_NAME'}	<b> : A unique name derived from the query params.</b></li>
 * <li>{@code 'HDR_FOUNDQUERY'}	<b> : A Boolean indicating whether this query was found in persistence.</b></li>
 * </ul>
 * </blockquote>
 */
public class QueryStringProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryStringProcessor.class);

    private final DB db;

    public QueryStringProcessor(DB db) {
	this.db = db;
    }

    @Override
    public void process(Exchange exchange) throws QueueException {
	LOGGER.debug("IN  - GET Headers(): " + exchange.getIn().getHeaders().keySet());
	LOGGER.debug("OUT - GET Headers(): " + exchange.getOut().getHeaders().keySet());

	final Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);
	exchange.getOut().setHeader("Q", request.getResourceRef().getQuery());

	final Map<String, String> params = request.getResourceRef().getQueryAsForm().getValuesMap();
	
	try {
    	    final VideoSearchRequest aQuery = new VideoSearchRequest();
    	    BeanUtils.copyProperties(aQuery, params);
    	    
    	    final String collectionName = PersistenceHelper.makeNameFromQuery(params);
    	    final Optional<VideoSearchRequest> needleState =  cacheContains(aQuery, collectionName);
    	    
    	    exchange.getOut().setHeader(PersistenceHelper.HDR_FOUNDQUERY, needleState.isPresent());
    	    exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, needleState.orElse(aQuery) );
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
