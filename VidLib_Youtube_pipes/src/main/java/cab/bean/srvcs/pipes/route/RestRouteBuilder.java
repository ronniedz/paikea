package cab.bean.srvcs.pipes.route;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.processor.CacheNewVideosProcessor;
import cab.bean.srvcs.pipes.processor.CachePullProcessor;
import cab.bean.srvcs.pipes.processor.QueryStringProcessor;

import com.mongodb.DB;
import com.mongodb.Mongo;


/**
 * A Camel Java DSL Router
 */
public class RestRouteBuilder extends RouteBuilder {

    private Mongo mongoBean = null;
    private DB db = null;

    private Predicate cacheCheckPredicate = null;
    private Processor cachePullProcessor = null;
    private Processor queryStringProcessor;

    private Processor stashNovelDataProcessor;

    public RestRouteBuilder() {
	super();
	// enable Jackson json type converter
	getContext().getProperties().put("CamelJacksonEnableTypeConverter", "true");
	// allow Jackson json to convert to pojo types also (by default jackson only converts to String and other simple types)
	getContext().getProperties().put("CamelJacksonTypeConverterToPojo", "true");
    }

    public RestRouteBuilder(Mongo mongoBean, String dbName) {
	this();
	this.mongoBean = mongoBean;
	this.db = mongoBean.getDB(dbName);
	this.cachePullProcessor = new CachePullProcessor(db);
	this.stashNovelDataProcessor = new CacheNewVideosProcessor(db);
	this.queryStringProcessor = new QueryStringProcessor(db);
    }

    /**
     * Camel routing rules in Java...
     */
    public void configure() {

	restConfiguration().component("restlet").host("localhost").port(7070)
		.bindingMode(RestBindingMode.auto)
		.componentProperty("chunked", "true");

	rest().path("ix").get("/a/b")
//	   .outTypeList(MQVideo.class)
	    .produces("application/json")
	    .bindingMode(RestBindingMode.json)
	    .route()
	   	.process(queryStringProcessor)
	   	.choice()
	   	   .when(header(PersistenceHelper.HDR_FOUNDQUERY_NAME).isNotNull())
	   	   	.to("direct:inbound_cached")
	   	   .otherwise()
	   	   	.to("direct:inbound_novel")
	   	.endChoice()
	    .end();
		
	from("direct:inbound_cached")
	    .process(cachePullProcessor)  ;
	
	from("direct:inbound_novel")
	    .process("ytAPICallProcessor")
	    .wireTap("direct:store_novel_data", true);

	from("direct:store_novel_data")
	    .process(stashNovelDataProcessor)
	    .end();
    }

    public Predicate getCacheCheckPredicate() {
	return cacheCheckPredicate;
    }

    public void setCacheCheckPredicate(Predicate cacheCheckPredicate) {
	this.cacheCheckPredicate = cacheCheckPredicate;
    }

    public Processor getCachePullProcessor() {
	return cachePullProcessor;
    }

    public void setCachePullProcessor(Processor cachePullProcessor) {
	this.cachePullProcessor = cachePullProcessor;
    }

    public Mongo getMongoBean() {
	return mongoBean;
    }

    public void setMongoBean(Mongo mongoBean) {
	this.mongoBean = mongoBean;
    }

}
