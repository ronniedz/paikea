package cab.bean.srvcs.pipes.route;

import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;
import org.restlet.Request;

import cab.bean.srvcs.pipes.Configuration;
import cab.bean.srvcs.pipes.Configuration.RestServerConfiguration;
import cab.bean.srvcs.pipes.Configuration.YoutubeResourceConfiguration;
import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.pipes.processor.CacheNewVideosProcessor;
import cab.bean.srvcs.pipes.processor.CachePullProcessor;
import cab.bean.srvcs.pipes.processor.Http4Processor;
import cab.bean.srvcs.pipes.processor.QueryStringProcessor;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;

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
    private final Configuration configuration;
    private final ObjectMapper objectMapper;
//    private final YouTubeAgent youTubeAgent;

    public RestRouteBuilder(DB mongoDb,  Configuration configuration, ObjectMapper objectMapper) {
	super();
	this.objectMapper = objectMapper;
	this.configuration = configuration;
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

	final RestServerConfiguration  serverConfig = configuration.getRestServerConfiguration();
	final YoutubeResourceConfiguration  ytApiConfig = configuration.getYoutubeResourceConfiguration();
	
	System.out.println("ytApiConfig : " + ytApiConfig);
	System.out.println("serverConfig : " + serverConfig);
	final String ytContext = String.format("https4://%s%s" , ytApiConfig.getHost(), ytApiConfig.getContextPath());
	
	restConfiguration().component("restlet")
		.host(serverConfig.getHost())
		.port(serverConfig.getPort())
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
            		final Message IN = exchange.getIn();
            		final Message OUT = exchange.getOut();
            		String Q = IN.getHeader("Q" , String.class);
            		
            		OUT.setHeader(Exchange.HTTP_QUERY,  Q);
            		OUT.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET));
            		OUT.setHeader("servicePath", simple("inbound_novella"));
            		
            		OUT.setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, IN.getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME));
            		OUT.setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, IN.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA));
            		OUT.setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, IN.getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME));
            		OUT.setHeader(PersistenceHelper.HDR_FOUNDQUERY, IN.getHeader(PersistenceHelper.HDR_FOUNDQUERY));
            	    }
            	}
	)
	.to(String.format(ytContext + "%s" ,  ytApiConfig.getVideoSearchPath() ) 
		+ "?"
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
	.process(
		new Processor() {
            	    @Override
            	    public void process(Exchange exchange) throws Exception {
            		final Message IN = exchange.getIn();
            		final Message OUT = exchange.getOut();
            		
            		final Request request = IN.getHeader(RestletConstants.RESTLET_REQUEST, Request.class);

            		String Q = request.getResourceRef().getQuery();
            		
            		OUT.setHeader(Exchange.HTTP_QUERY,  Q);
            		OUT.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET));
            		
            		OUT.setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, IN.getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME));
            		OUT.setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, IN.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA));
            		OUT.setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, IN.getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME));
            		OUT.setHeader(PersistenceHelper.HDR_FOUNDQUERY, IN.getHeader(PersistenceHelper.HDR_FOUNDQUERY));
            	    }
            	}
	)
	.to(String.format(ytContext + "%s" ,  ytApiConfig.getVideoDetailPath() ) 
		+ "?"
		+ "disableStreamCache=true"
		+ "&copyHeaders=true"
		+ "&transferException=true"
		+ "&useSystemProperties=true"
	)
//	.to("log:target/dump.txt?showBody=true&showHeaders=true")

	.process(new  Processor () {
	    @Override
	    public void process(Exchange exchange) throws Exception {
		final Message IN = exchange.getIn();
		final Message OUT = exchange.getOut();
		YouTubeVideoDetailResponse ytResp = objectMapper.readValue((InputStream)IN.getBody(), YouTubeVideoDetailResponse.class);
		OUT.setBody(ytResp);
	    }
	    
	})
	.end();
    }

}
