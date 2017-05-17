package cab.bean.srvcs.pipes.processor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.mongojack.JacksonDBCollection;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.QueueException;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.core.MongoVideo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class CachePullProcessor implements Processor {
    private static final Log LOGGER = LogFactory.getLog(CachePullProcessor.class);
    private final DB db;
    
    public CachePullProcessor(DB db) {
	this.db = db;
    }

    @Override
    public void process(Exchange exchange) throws QueueException {

	LOGGER.debug("IN  - GET Headers(): " + exchange.getIn().getHeaders().keySet());
	LOGGER.debug("OUT - GET Headers(): " + exchange.getOut().getHeaders().keySet());
	
	try {
	
	    final VideoSearchRequest outQuery = (VideoSearchRequest) exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA);
	    
	    final Map<String, String> params = exchange.getIn().getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, Map.class);
	    
	    final YouTubeResponse youTubeResponse =  new YouTubeResponse();		
	    
	    BeanUtils.copyProperties(youTubeResponse, outQuery);
	    
	    final DBObject query = StringUtils.isBlank(outQuery.getLastId()) ? new BasicDBObject() : new BasicDBObject("_id", new BasicDBObject("$gte", new ObjectId(outQuery.getLastId()))); 
	    
	    final JacksonDBCollection<MongoVideo, String> jVidColl = JacksonDBCollection.wrap(
		    db.getCollection(exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class)),
		    MongoVideo.class, String.class);
	    
	    youTubeResponse.setItems(
		    jVidColl.find(query).limit(Integer.parseInt(Optional.ofNullable(params.get("maxResults")).orElse(PersistenceHelper.DEFAULT_FETCH_SIZE)))
		    .toArray());
	
	    exchange.getOut().setBody(youTubeResponse);
	}
	catch ( IllegalAccessException | InvocationTargetException e ) {
	    throw new QueueException(e.getMessage());
	}
    }
}
