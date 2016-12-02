package cab.bean.srvcs.pipes.processor;

import java.util.ArrayList;
import java.util.List;

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
import cab.bean.srvcs.tube4kids.core.VideoType;
import cab.bean.srvcs.tube4kids.core.MongoVideo;
import cab.bean.srvcs.tube4kids.core.MongoVideo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class CacheNewVideosProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheNewVideosProcessor.class);
    private DB db;
    private JacksonDBCollection<VideoSearchRequest, String> qryAllocColl = null;

    public CacheNewVideosProcessor(DB db) {
	this.db = db;
	this.qryAllocColl = JacksonDBCollection.wrap(db.getCollection(PersistenceHelper.META_DATA_DB_NAME), VideoSearchRequest.class, String.class);
    }
    
    public void process(Exchange exchange) throws Exception {

	YouTubeResponse ytResp = exchange.getIn().getBody(YouTubeResponse.class);
	String collectionName = exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
	VideoSearchRequest queryDetail = exchange.getIn().getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, VideoSearchRequest.class);

	BeanUtils.copyProperties(queryDetail, ytResp);
	
	DBCollection coll = db.getCollection(collectionName);
	
	JacksonDBCollection<MongoVideo, org.bson.types.ObjectId> jVidColl = JacksonDBCollection.wrap(coll, MongoVideo.class, org.bson.types.ObjectId.class);
	
	List<MongoVideo> insertsVids = new ArrayList<MongoVideo>();
	
	for ( VideoType tvid : ytResp.getItems()) {
	    MongoVideo mg = new MongoVideo();
	    BeanUtils.copyProperties(mg, tvid);
	    insertsVids.add(mg);
	}
	WriteResult<MongoVideo, org.bson.types.ObjectId> res = jVidColl.insert(insertsVids);
	
	queryDetail.setCollectionName(collectionName);
	org.bson.types.ObjectId lid = res.getSavedId();
	queryDetail.setLastId(lid.toString());
	
	WriteResult<VideoSearchRequest, String> result = qryAllocColl.insert(queryDetail);
	
	LOGGER.debug("Saved " + result.getSavedIds().size() + " videos");

    }
}

