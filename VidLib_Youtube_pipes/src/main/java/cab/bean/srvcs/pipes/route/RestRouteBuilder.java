package cab.bean.srvcs.pipes.route;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;

import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.processor.CacheNewVideosProcessor;
import cab.bean.srvcs.pipes.processor.CachePullProcessor;
import cab.bean.srvcs.pipes.processor.QueryStringProcessor;

import com.mongodb.DB;


/**
 * A Camel Java DSL Router
 */
public class RestRouteBuilder extends RouteBuilder {

//    private Mongo mongoBean = null;

    private Predicate cacheCheckPredicate = null;
    private Processor cachePullProcessor = null;
    private Processor queryStringProcessor;
    private Processor stashNovelDataProcessor;
    
    private String	resourcePath ;
    private String	serviceUri;
    private int		restServerPort;
    private String	restServerHost;

    public RestRouteBuilder() {
	super();
	// enable Jackson json type converter
	getContext().getProperties().put("CamelJacksonEnableTypeConverter", "true");
	// allow Jackson json to convert to pojo types also (by default jackson only converts to String and other simple types)
	getContext().getProperties().put("CamelJacksonTypeConverterToPojo", "true");
    }

    public RestRouteBuilder(DB db) {
	this();
	this.cachePullProcessor = new CachePullProcessor(db);
	this.stashNovelDataProcessor = new CacheNewVideosProcessor(db);
	this.queryStringProcessor = new QueryStringProcessor(db);
    }

    /**
     * Camel routing rules in Java...
     */
    @SuppressWarnings("deprecation")
    public void configure() {

	restConfiguration().component("restlet")
		.host(this.restServerHost)
		.port(this.restServerPort)
		.bindingMode(RestBindingMode.auto)
		.componentProperty("chunked", "true");

	RestDefinition  def = rest(this.resourcePath);

	def.get("/search")
		.produces("application/json")
		.bindingMode(RestBindingMode.json)
            	.route()
            		.routeId("search")
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
		.setHeader("servicePath", simple("inbound_novella"))

	    .process("ytAPICallProcessor")
	    .wireTap("direct:store_novel_data", true);

	from("direct:store_novel_data")
	    .process(stashNovelDataProcessor)
	    .end();

	def.get("/detail")
	.produces("application/json")
	.bindingMode(RestBindingMode.json)
	.route()
	.routeId("detail")
//	.setHeader("servicePath", simple("/detail"))
	.process("ytAPICallProcessor")
	.end();


    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public int getRestServerPort() {
        return restServerPort;
    }

    public void setRestServerPort(int restServerPort) {
        this.restServerPort = restServerPort;
    }

    public String getRestServerHost() {
        return restServerHost;
    }

    public void setRestServerHost(String restServerHost) {
        this.restServerHost = restServerHost;
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
//
//    public Mongo getMongoBean() {
//	return mongoBean;
//    }
//
//    public void setMongoBean(Mongo mongoBean) {
//	this.mongoBean = mongoBean;
//    }

}
