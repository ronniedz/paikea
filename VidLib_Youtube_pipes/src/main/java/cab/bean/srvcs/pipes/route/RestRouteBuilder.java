package cab.bean.srvcs.pipes.route;

import java.io.InputStream;

import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;

import cab.bean.srvcs.pipes.Configuration;
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

    /**
     * The following values are set in Spring beans.
     */
    private final  Processor cachePullProcessor;
    private final  Processor queryStringProcessor;
    private final  Processor stashNovelDataProcessor;
    
    private final Configuration.RestServerConfiguration configuration;

    private static final String GAPI_Agent = "ytAPICallProcessor";

    public RestRouteBuilder(DB mongoDb,  Configuration configuration) {
	super();
	
	this.configuration = configuration.getRestServerConfiguration();

	this.cachePullProcessor = new CachePullProcessor(mongoDb);
	this.stashNovelDataProcessor = new CacheNewVideosProcessor(mongoDb);
	this.queryStringProcessor = new QueryStringProcessor(mongoDb);
	
	// enable Jackson json type converter
	getContext().getProperties().put("CamelJacksonEnableTypeConverter", "true");
	// allow Jackson json to convert to pojo types also (by default jackson only converts to String and other simple types)
	getContext().getProperties().put("CamelJacksonTypeConverterToPojo", "true");
    }

    /**
     * Routes ...
     */
    @SuppressWarnings("deprecation")
    public void configure() {

	restConfiguration().component("restlet")
		.host(this.configuration.getHost())
		.port(this. configuration.getPort())
		.bindingMode(RestBindingMode.auto)
		.componentProperty("chunked", "true");

	RestDefinition  def = rest(configuration.getContextPath());

	def.get(configuration.getSearchServicePath())
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

	def.get(configuration.getDetailServicePath())
	.produces("application/json")
	.bindingMode(RestBindingMode.json)
	.route()
	.routeId("detail")
	.process(GAPI_Agent)
	.end();
    }

}
