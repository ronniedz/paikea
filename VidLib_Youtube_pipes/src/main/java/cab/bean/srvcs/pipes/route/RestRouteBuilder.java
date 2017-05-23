package cab.bean.srvcs.pipes.route;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;

import cab.bean.srvcs.pipes.Configuration;
import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.processor.CacheNewVideosProcessor;
import cab.bean.srvcs.pipes.processor.CachePullProcessor;
import cab.bean.srvcs.pipes.processor.Http4Processor;
import cab.bean.srvcs.pipes.processor.QueryStringProcessor;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final CachePullProcessor cachePullProcessor;
    private final QueryStringProcessor queryStringProcessor;
    private final CacheNewVideosProcessor stashNovelDataProcessor;
    private final String ytAPICallProcessor = "ytAPICallProcessor";
    private final Configuration.RestServerConfiguration serverConfig;
    private final ObjectMapper objectMapper;
//    private final YouTubeAgent youTubeAgent;

    public RestRouteBuilder(DB mongoDb,  Configuration configuration, ObjectMapper objectMapper) {
	super();
	this.objectMapper = objectMapper;
	this.serverConfig = configuration.getRestServerConfiguration();
//	this.youTubeAgent = new YouTubeAgentImpl(configuration.getYoutubeResourceConfiguration());
//	this.ytAPICallProcessor = new YouTubeAPICallProcessor(youTubeAgent);
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
		.host(this.serverConfig.getHost())
		.port(this. serverConfig.getPort())
		.bindingMode(RestBindingMode.auto)
		.componentProperty("chunked", "true");

	RestDefinition  def = rest(serverConfig.getContextPath());

	def.get(serverConfig.getSearchServicePath())
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
	.process(
		new Processor() {
            	    @Override
            	    public void process(Exchange exchange) throws Exception {
            		org.apache.camel.Message messageIn = exchange.getIn();
            		org.apache.camel.Message message = exchange.getOut();

            		String Q = messageIn.getHeader("Q" , String.class);
            		
            		message.setHeader(Exchange.HTTP_QUERY,  Q);
            		message.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET));
            		message.setHeader("servicePath", simple("inbound_novella"));
            		
            		message.setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, messageIn.getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME));
            		message.setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, messageIn.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA));
            		message.setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, messageIn.getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME));
            		message.setHeader(PersistenceHelper.HDR_FOUNDQUERY, messageIn.getHeader(PersistenceHelper.HDR_FOUNDQUERY));
            	    }
            	}
	)
	.to("https4://www.googleapis.com/youtube/v3/search?"
		+ "disableStreamCache=true"
		+ "&copyHeaders=true"
		+ "&transferException=true"
		+ "&useSystemProperties=true"
	)
//	.to("log:target/dump.txt?showBody=true&showHeaders=true")
	.process(new Http4Processor(objectMapper))
	.wireTap("direct:store_novel_data", true)
	;

	from("direct:store_novel_data")
	    .process(stashNovelDataProcessor)
	    .end();

	def.get(serverConfig.getDetailServicePath())
	.produces("application/json")
	.bindingMode(RestBindingMode.json)
	.route()
	.routeId("detail")
	.process(ytAPICallProcessor)
	.end();
    }

}
