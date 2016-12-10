package cab.bean.srvcs.tube4kids.remote;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;

import com.fasterxml.jackson.datatype.joda.JodaMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Forwards requests to a video search-request queue. The queue directs whether the request 
 * is fulfilled by Y.T. or from local store. 
 *
 */
public class YouTubeAPIProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAPIProxy.class);

    private Client jClient = null;
    private WebTarget baseTarget = null;
    // Values injected by config
    private String searchPath = null;
    private String hostUrl = null;
    private JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();

   /**
    *	 @TODO  - Export the values below to config
    */
    private static final String HDR_NAME_SERVICE_DEST = "X-ServiceDestination";
    private static final String HDR_VALUE_SERVICE_DEST = "youTubeAgent";
    
    
    /**
     * @param url 
     */
    public YouTubeAPIProxy(URL url) {
	this.hostUrl =
		url.getProtocol()
		+ "://" +
		url.getHost() 
		+ ((url.getPort() > 0 ) ? ":" + url.getPort() : "");
	
	this.searchPath = url.getFile();

	jClient = ClientBuilder.newClient().register(jsonProvider);

	baseTarget = jClient.target(hostUrl);
    }

    /**
     * Executes a GET request to the given URL.
     * 
     * @param params
     * 		a hash of name/value pairs.
     * 
     * @return
     * 		the String body of the response.
     */
    public String runSearch (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(searchPath);
	
	// Add params to query
	webResource = putParamsInRequest(webResource, params);

	Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).header(HDR_NAME_SERVICE_DEST, HDR_VALUE_SERVICE_DEST).get(Response.class); 
	LOGGER.info("clientResponsegetStatus: {}", clientResponse.getStatus());
	
	String body = clientResponse.readEntity(String.class);
	
	return body;
    }
    
    public YouTubeVideoDetailResponse runVideoDetail (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path("/pipes/detail");
	
	// Add params to query
	webResource = putParamsInRequest(webResource, params);
	
	Response clientResponse = webResource
		.request(MediaType.APPLICATION_JSON)
		.get(Response.class);
	
	LOGGER.info("clientResponsegetStatus: {}", clientResponse.getStatus());
	
	YouTubeVideoDetailResponse body = clientResponse.readEntity(YouTubeVideoDetailResponse.class);
	
	return body;
    }
    
    public List<String>  runSearchList (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(searchPath);
	// Add params to query
	webResource = putParamsInRequest(webResource, params);
	
	Response clientResponse = webResource
		.request(MediaType.APPLICATION_JSON)
		.header(HDR_NAME_SERVICE_DEST, HDR_VALUE_SERVICE_DEST).get(Response.class); 
	LOGGER.info("clientResponsegetStatus: {}", clientResponse.getStatus());
	
	List<String>  body = clientResponse.readEntity(new GenericType<LinkedList<String>>() {});
	
	return body;
    }
    
    public YouTubeResponse  runSearchQuery (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(searchPath);
	
	// Add params to query
	YouTubeResponse qwrap = putParamsInRequest(webResource, params)
		.request(MediaType.APPLICATION_JSON)
		.header(HDR_NAME_SERVICE_DEST, HDR_VALUE_SERVICE_DEST).get(YouTubeResponse.class);
	
	return qwrap;
    }

    private static WebTarget putParamsInRequest(WebTarget webResource,  Map<String, String> params) {
	for (String k : params.keySet() )  webResource = webResource.queryParam(k, params.get(k) );
	return webResource;
    }
    
//    
//    private List<String> sortParamNames(Map<String, String> params) {
//	List<String> list = new LinkedList<String>(params.keySet());
//	list.sort(new Comparator<String>() {
//	    public int compare(String a, String b)  { return a.toUpperCase().compareTo(b.toUpperCase()); }
//	});
//	return list;
//    }

}


//javax.ws.rs.core.Response jsonResponse = client.target(url).request(MediaType.APPLICATION_JSON).get();
//Map<SomeClassOfYours> entitiesFromResponse = jsonResponse.readEntity(new GenericType<Map<SomeClassOfYours>>() {});
//SomeClassOfYours entityFromResponse = jsonResponse.readEntity(SomeClassOfYours.class);
//

