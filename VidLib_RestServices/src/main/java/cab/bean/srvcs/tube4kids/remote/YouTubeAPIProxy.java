package cab.bean.srvcs.tube4kids.remote;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.api.YouTubeVideoDetailResponse;
import cab.bean.srvcs.tube4kids.config.RestServerConfiguration;

/**
 * Forwards requests to a video search-request queue. The queue directs whether the request
 * is fulfilled by Y.T. or from local store.
 */
public class YouTubeAPIProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeAPIProxy.class);

    private final WebTarget baseTarget;
    private final RestServerConfiguration conf;


   /**
    *	 @TODO  - Export the values below to config
    */
    private static final String HDR_NAME_SERVICE_DEST = "X-ServiceDestination";
    private static final String HDR_VALUE_SERVICE_DEST = "youTubeAgent";

    /**
     * @param url
     */
    public YouTubeAPIProxy(Client jClient, RestServerConfiguration rsc, String apiKey) {
	this.conf = rsc;
        this.baseTarget = jClient.target(
        	new StringBuffer("http://").append(conf.getHost()).append(":")
        		.append(conf.getPort()).append(conf.getContextPath()).toString()
        	).queryParam("key", apiKey);
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
	WebTarget webResource = baseTarget.path(conf.getSearchServicePath());

	// Add params to query
	webResource = putParamsInRequest(webResource, params);

	Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
		.header(HDR_NAME_SERVICE_DEST, HDR_VALUE_SERVICE_DEST).get(Response.class);
	LOGGER.info("clientResponsegetStatus: {}", clientResponse.getStatus());

	return clientResponse.readEntity(String.class);

    }

    public YouTubeVideoDetailResponse runVideoDetail (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(conf.getDetailServicePath());

	// Add params to query
	webResource = putParamsInRequest(webResource, params);

	Response clientResponse = webResource
		.request(MediaType.APPLICATION_JSON)
		.get(Response.class);

	LOGGER.info("clientResponsegetStatus: {}", clientResponse.getStatus());
	return  clientResponse.readEntity(YouTubeVideoDetailResponse.class);
    }

    public List<String>  runSearchList (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(conf.getSearchServicePath());
	// Add params to query
	webResource = putParamsInRequest(webResource, params);

	Response clientResponse = webResource
		.request(MediaType.APPLICATION_JSON)
		.header(HDR_NAME_SERVICE_DEST, HDR_VALUE_SERVICE_DEST).get(Response.class);
	LOGGER.info("clientResponsegetStatus: {}", clientResponse.getStatus());

	return clientResponse.readEntity(new GenericType<LinkedList<String>>() {});
    }

    public YouTubeResponse  runSearchQuery (Map<String, String> params)  {
	WebTarget webResource = baseTarget.path(conf.getSearchServicePath());

	// Add params to query
	return  putParamsInRequest(webResource, params)
		.request(MediaType.APPLICATION_JSON)
		.header(HDR_NAME_SERVICE_DEST, HDR_VALUE_SERVICE_DEST).get(YouTubeResponse.class);
    }

    private static WebTarget putParamsInRequest(WebTarget webResource,  Map<String, String> params) {
	for (String k : params.keySet() )  webResource = webResource.queryParam(k, params.get(k) );
	return webResource;
    }
}
