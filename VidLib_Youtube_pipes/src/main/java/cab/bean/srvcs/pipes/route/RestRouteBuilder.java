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
 * Route requests received at the Restlet endpoint. "Search" requests
 * can be fulfilled from cache (in MongoDB) or by the
 * GAPI_Agent (configured in spring context)
 * 
 * The current endpoints to the Restlet are:
 * 
 * 	'/search'	- to search for videos
 * 	'/detail'	- to get details for a specified video
 *  
 */
public class RestRouteBuilder extends RouteBuilder {

    private static final String srv_endpt_search = "/search";
    private static final String srv_endpt_detail = "/detail";

    private static final String GAPI_Agent = "ytAPICallProcessor";
    /**
     * The following values are set in Spring beans.
     */
    private Predicate	cacheCheckPredicate = null;
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

    /**
     * @param mongoDb currently used only for caching queries and video refs/info.
     */
    public RestRouteBuilder(DB mongoDb) {
	this();
	this.cachePullProcessor = new CachePullProcessor(mongoDb);
	this.stashNovelDataProcessor = new CacheNewVideosProcessor(mongoDb);
	this.queryStringProcessor = new QueryStringProcessor(mongoDb);
    }

    /**
     * Routing rules ...
     */
    @SuppressWarnings("deprecation")
    public void configure() {

	restConfiguration().component("restlet")
		.host(this.restServerHost)
		.port(this.restServerPort)
		.bindingMode(RestBindingMode.auto)
		.componentProperty("chunked", "true");

	RestDefinition  def = rest(this.resourcePath);

	def.get(srv_endpt_search)
		.produces("application/json")
		.bindingMode(RestBindingMode.json)
            	.route()
            		.routeId("search")
            		.process(queryStringProcessor)
            		.choice()
            	   	    .when(header(PersistenceHelper.HDR_FOUNDQUERY).isEqualTo(Boolean.TRUE))
            	   	    	.to("direct:inbound_cached")
            	   	    .otherwise()
            	   	   	.to("direct:inbound_novel")
            	   	   .endChoice()
            	.end();
		
	from("direct:inbound_cached")
	    .process(cachePullProcessor)
	;
	
	from("direct:inbound_novel")
	    .setHeader("servicePath", simple("inbound_novella"))

	    .process(GAPI_Agent)
	    .wireTap("direct:store_novel_data", true);

	from("direct:store_novel_data")
	    .process(stashNovelDataProcessor)
	    .end();

	def.get(srv_endpt_detail)
	.produces("application/json")
	.bindingMode(RestBindingMode.json)
	.route()
	.routeId("detail")
//	.setHeader("servicePath", simple("/detail"))
	.process(GAPI_Agent)
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
}
