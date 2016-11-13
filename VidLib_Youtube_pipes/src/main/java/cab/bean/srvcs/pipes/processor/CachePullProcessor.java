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
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.core.MongoVideo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class CachePullProcessor implements Processor {
    private static Log LOGGER = LogFactory.getLog(CachePullProcessor.class);
    private DB db;
    
    public CachePullProcessor(DB db) {
	this.db = db;
    }

    public void process(Exchange exchange) throws Exception {

	LOGGER.debug("IN  - GET Headers(): " + exchange.getIn().getHeaders().keySet());
	LOGGER.debug("OUT - GET Headers(): " + exchange.getOut().getHeaders().keySet());
	
	VideoSearchRequest outQuery = (VideoSearchRequest) exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA);
	String collectionName = exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
	
	Map<String, String> params = exchange.getIn().getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, Map.class);

	YouTubeResponse youTubeResponse =  new YouTubeResponse();		

	try {
	    BeanUtils.copyProperties(youTubeResponse, outQuery);
	} catch (IllegalAccessException | InvocationTargetException e) {
	    e.printStackTrace();
	}

        DBObject query = new BasicDBObject(); 
    	    
        if (! StringUtils.isBlank(outQuery.getLastId())) {
            query = new BasicDBObject("_id", new BasicDBObject("$gte", new ObjectId(outQuery.getLastId())));
        }

	DBCollection coll = db.getCollection(collectionName);
	JacksonDBCollection<MongoVideo, String> jVidColl = JacksonDBCollection.wrap(coll, MongoVideo.class, String.class);
	
	org.mongojack.DBCursor<MongoVideo> cur = jVidColl.find(query).limit(Integer.parseInt(Optional.ofNullable(params.get("maxResults")).orElse(PersistenceHelper.DEFAULT_FETCH_SIZE)));

	List<MongoVideo> items = new ArrayList();
	while( cur.hasNext()) {
	    items.add(cur.next());
	}
	youTubeResponse.setItems(items);
	
	exchange.getOut().setBody(youTubeResponse);
    }
}
