package cab.bean.srvcs.pipes.route;

import java.io.InputStream;
import java.util.function.BiFunction;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.restlet.RestletConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestDefinition;
import org.restlet.Request;

import cab.bean.srvcs.pipes.Configuration;
import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.processor.CacheNewVideosProcessor;
import cab.bean.srvcs.pipes.processor.CachePullProcessor;
import cab.bean.srvcs.pipes.processor.Http4Processor;
import cab.bean.srvcs.pipes.processor.QueryStringProcessor;
import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;
import cab.bean.srvcs.tube4kids.config.RestServerConfiguration;
import cab.bean.srvcs.tube4kids.config.YoutubeResourceConfiguration;

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

    public RestRouteBuilder(DB mongoDb,  Configuration configuration, ObjectMapper objectMapper) {
	super();
	this.objectMapper = objectMapper;
	this.configuration = configuration;

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

	final String ytContext = String.format("https4://%s%s%s"
		+ "?disableStreamCache=true&transferException=true&useSystemProperties=true",
		ytApiConfig.getHost(), ytApiConfig.getContextPath(), "%s");

	final String youtubeSearchURL = String.format(ytContext,  ytApiConfig.getVideoSearchPath());
	final String youtubeDetailsURL = String.format(ytContext,  ytApiConfig.getVideoDetailPath());


	restConfiguration().component("restlet")
		.host(serverConfig.getHost())
		.port(serverConfig.getPort())
		.bindingMode(RestBindingMode.auto)
		.componentProperty("chunked", "true");

	RestDefinition  def = rest(serverConfig.getContextPath());

	/**
	 * Receive Video query
	 */
	def.get(serverConfig.getSearchServicePath())
	.produces("application/json")
	.bindingMode(RestBindingMode.json)
	.route()
	.routeId("search")
		.process(queryStringProcessor)  // Creates a VideoSearch object, determines "novelty" of the query
            	.choice()
    	   	    .when(header(PersistenceHelper.HDR_FOUNDQUERY).isEqualTo(Boolean.TRUE))
    	   	    	.to("direct:inbound_cached")
    	   	    .otherwise()
    	   	   	.to("direct:inbound_novel")
    	   	.endChoice()
    	.end();
	// Pull from cache if a repeated query
	from("direct:inbound_cached").process(cachePullProcessor);

	/**
	 * A function to copy state  and data headers
	 */
	final BiFunction<Message, Message, Message> forwardHeaders = (Message min,  Message mout) -> {
	    for (String hdr : new String[] {
		    PersistenceHelper.HDR_QUERYPARAMS_NAME, PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA,
		    PersistenceHelper.HDR_NAME_COLLECTION_NAME, PersistenceHelper.HDR_FOUNDQUERY, PersistenceHelper.REST_QUERYSTRING}) {
		Object obj = min.getHeader(hdr);
		if (obj != null) {
		    mout.setHeader(hdr, obj);
		}
	    }
	    return mout;
	};

	from("direct:inbound_novel")
	// Prepare query for remote YT query
        	.process( exchange -> {
        	    final Message mout = exchange.getOut();
        	    final Message min = exchange.getIn();

        	    forwardHeaders.apply(min, mout);

        	    mout.setHeader("servicePath", simple("inbound_novella"));

        	    final Object queryString = exchange.getIn().getHeader(PersistenceHelper.REST_QUERYSTRING);
        	    if (queryString != null) {
        		mout.setHeader(Exchange.HTTP_QUERY, queryString);
        	    }
        	    mout.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET));
        	})
        	/*
        	 * This is a call to  {@code http4} component
        	 */
	.to(youtubeSearchURL)
	// process the response
	.process(new Http4Processor(objectMapper))

	// send a copy of the response to be cached in the DB
	.wireTap("direct:store_novel_data", true);

	from("direct:store_novel_data")
	.process(stashNovelDataProcessor)
	.end();

	// Lookup Video detail from YT
	def.get(serverConfig.getDetailServicePath())
	.produces("application/json")
	.bindingMode(RestBindingMode.json)
	.route()
	.routeId("detail")
        	.process( exchange -> {
        	    final Message mout = exchange.getOut();
        	    final Message min = exchange.getIn();

        	    forwardHeaders.apply(min, mout);

        	    final Request request = exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class);
        	    if (request != null ) {
        		exchange.getOut().setHeader(Exchange.HTTP_QUERY,  request.getResourceRef().getQuery());
        	    }
        	    mout.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET));
        	})
	.to(youtubeDetailsURL)
//	.to("log:?showBody=true&showHeaders=true")
        	.process( exchange -> {
        	    final Message IN = exchange.getIn();
        	    final Message OUT = exchange.getOut();
        	    OUT.setBody(objectMapper.readValue((InputStream)IN.getBody(), YouTubeVideoDetailResponse.class));
        	})
	.end();
    }

}
