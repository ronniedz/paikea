package cab.bean.srvcs.pipes.processor;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.beanutils.BeanUtils;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.core.MongoVideo;

import com.mongodb.DB;
import com.mongodb.DBCollection;

public class CacheNewVideosProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheNewVideosProcessor.class);
    private final DB db;
    private final JacksonDBCollection<VideoSearchRequest, String> qryAllocColl;

    public CacheNewVideosProcessor(DB db) {
	this.db = db;
	this.qryAllocColl = JacksonDBCollection.wrap(
		db.getCollection(PersistenceHelper.META_DATA_DB_NAME),
		VideoSearchRequest.class, String.class);
    }
    
    /**
     * Persists new Videos from message
     */
    @Override
    public void process(Exchange exchange) throws Exception {

	final YouTubeResponse ytResp = exchange.getIn().getBody(YouTubeResponse.class);
	
	final String collectionName = exchange.getIn()
		.getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
	
	final VideoSearchRequest queryDetail = exchange.getIn()
		.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, VideoSearchRequest.class);

	BeanUtils.copyProperties(queryDetail, ytResp);

	queryDetail.setCollectionName(collectionName);

	final DBCollection coll = db.getCollection(collectionName);
	
	final JacksonDBCollection<MongoVideo, org.bson.types.ObjectId> jVidColl =
		JacksonDBCollection.wrap(coll, MongoVideo.class, org.bson.types.ObjectId.class);
	
	final WriteResult<MongoVideo, org.bson.types.ObjectId> res = jVidColl.insert(
		ytResp.getItems().stream().map( item -> {
		    MongoVideo mg = new MongoVideo();
		    try { BeanUtils.copyProperties(mg, item); }
		    catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
		    return mg;
		}).collect(Collectors.toList())
	);
	
	queryDetail.setLastId(res.getSavedId().toString());
	
	WriteResult<VideoSearchRequest, String> result = qryAllocColl.insert(queryDetail);
	
	LOGGER.debug("Saved " + result.getSavedIds().size() + " videos");

    }
}

