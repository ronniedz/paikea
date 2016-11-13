package cab.bean.srvcs.tube4kids.resources;

import java.util.Map;
import javax.ws.rs.core.Response;

public interface YouTubeAgent {

    /**
     * A YouTube API key is required and may also be set in the constructor.
     * 
     * @param apiKey
     * 		a String developer key provided by YouTube.
     */
    public void setApiKey(String apiKey);

    /**
     * Sends a GET request to YouTube Rest API.  
     * 
     * @param params
     * 		the set of name-value pairs for the query 
     * 
     * @return
     * 		a JSON response
     */
    public Response runSearchQuery(Map<String, String> params);
  
}
