package cab.bean.srvcs.pipes.processor;

import java.lang.reflect.InvocationTargetException;
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
import cab.bean.srvcs.pipes.model.VideoSearchRequest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class QueryStringProcessor implements Processor {
    private static Log LOGGER = LogFactory.getLog(QueryStringProcessor.class);

    private DB db;

    public QueryStringProcessor(DB db) {
	this.db = db;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
	LOGGER.debug("IN  - GET Headers(): " + exchange.getIn().getHeaders().keySet());
	LOGGER.debug("OUT - GET Headers(): " + exchange.getOut().getHeaders().keySet());

	Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);

	Map<String, String> params = request.getResourceRef().getQueryAsForm().getValuesMap();

	VideoSearchRequest aQuery = new VideoSearchRequest();
	try {
	    BeanUtils.copyProperties(aQuery, params);
	} catch (IllegalAccessException | InvocationTargetException e) {
	    e.printStackTrace();
	}

	String collectionName = PersistenceHelper.determineName(params);
	
	VideoSearchRequest found = null;
	if ( (found = cacheContains(aQuery, collectionName)) != null ) {
	    exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, found );
	    exchange.getOut().setHeader(PersistenceHelper.HDR_FOUNDQUERY_NAME, Boolean.TRUE);
//	    exchange.getIn().setHeader(PersistenceHelper.HDR_FOUNDQUERY_NAME, Boolean.TRUE);
	    // The Java DSL route needs me to set the 'found' flag in the IN-message 
	}
	else {
	    exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA,aQuery);
	}

	exchange.getOut().setBody(aQuery);

	exchange.getOut().setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, params);
	
	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME,collectionName);
    }

    private VideoSearchRequest cacheContains(VideoSearchRequest queryObj, String collectionName) {
        VideoSearchRequest foundSearchResponse = null;

	if ( db.collectionExists(collectionName) ) {
	    JacksonDBCollection<VideoSearchRequest, org.bson.types.ObjectId> metadat = JacksonDBCollection.wrap(
		    db.getCollection(PersistenceHelper.META_DATA_DB_NAME),
		    VideoSearchRequest.class,
		    org.bson.types.ObjectId.class
	    );
            DBObject query = new BasicDBObject();
            
//            if (StringUtils.isNotBlank(queryObj.getPageToken())) {
        		query.put("pageToken" , Optional.ofNullable(queryObj.getPageToken()).orElse(""));
        		query.put("collectionName" , collectionName);
//            }
            foundSearchResponse = metadat.findOne(query);
           
	}
	return foundSearchResponse;
    }

}
