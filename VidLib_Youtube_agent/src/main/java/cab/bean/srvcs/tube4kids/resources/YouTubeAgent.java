package cab.bean.srvcs.tube4kids.resources;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import cab.bean.srvcs.tube4kids.core.BasicVideo;
import cab.bean.srvcs.tube4kids.core.MongoVideo;

public interface YouTubeAgent {

    /**
     * A YouTube API key is required and may also be set in the constructor.
     * 
     * @param apiKey
     * 		a String developer key provided by YouTube.
     */
    public void setApiKey(String apiKey);

    /**
     * `Sends a GET request to YouTube Rest API.  
     * 
     * @param params
     * 		the set of name-value pairs for the query 
     * 
     * @return
     * 		the JSON response from YouTube as a String
     */
    public String runSearch(Map<String, String> params);
    
    public Response runSearchQuery(Map<String, String> params) ;
    
    public List runSearchList(Map<String, String> params) ;
  
}
