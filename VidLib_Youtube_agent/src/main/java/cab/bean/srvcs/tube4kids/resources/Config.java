package cab.bean.srvcs.tube4kids.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.WebTarget;

import jersey.repackaged.com.google.common.collect.ImmutableList;

/**
 * @author ronalddennison
 *
 */
public class Config {

    public String APIKEY = "AIzaSyAmOXO8tcUauYkj1POSnEzle_Rm61LAOes";
    public String DATASRC_DOMAIN = "https://www.googleapis.com";
    public String DATASRC_CTX_URL = "/youtube/v3";
    public String DATASRC_SRV_URL = "/search";

    
//    public static final String HDR_NAME_SERVICE_DEST_DATA = "X-dbstore_data";
//    public static final String HDR_NAME_SERVICE_DEST = "X-ServiceDestination";
    public static final String HDR_VALUE_SERVICE_DEST = "youTubeAgent";
    
    /**
     * Test parameters of a YT query.
     * <pre>
            part=snippet
            maxResults=10
            order=date
            publishedAfter=2006-01-01T00%3A00%3A00Z
            q=filmes+completo+dublado+-legendado
            regionCode=BR
            relevanceLanguage=PT
            type=video
            videoCaption=any
            videoDuration=long
            videoLicense=youtube
            videoType=any
            fields=etag%2Citems%2Ckind%2CnextPageToken%2CpageInfo
            key=AIzaSyAmOXO8tcUauYkj1POSnEzle_Rm61LAOe
     </pre>
     */
    public final QVal  testParams = new QVal()
	.xput("q", "filmes completo dublado -legendado")
	.xput("part", "snippet")
	.xput("maxResults", "2")
	.xput("order", "date")
	.xput("publishedAfter", "2006-01-01T00:00:00Z")
	.xput("regionCode", "BR")
	.xput("relevanceLanguage", "PT")
	.xput("type", "video")
	.xput("videoCaption", "any")
	.xput("videoDuration", "long")
	.xput("videoLicense", "youtube")
	.xput("videoType", "any")
	.xput("fields", "etag,items,kind,nextPageToken,prevPageToken,pageInfo");

    
    /**
     * 
     */
    public Config() {
	super();
	loadSettings();
    }

    private void loadSettings() {
	Properties prop = new Properties();
	InputStream input = null;

	final String dataSrcDomain_KEY	= "dataSrcDomain";
	final String dataSrcCtxUri_KEY	= "dataSrcCtxUri";
	final String dataSrcUri_KEY		= "dataSrcUri";
	final String apiKey_KEY			= "apiKey";

	try {

	    String filename = "config.properties";
	    input = Config.class.getResourceAsStream(filename);

	    if (input == null) {
		System.out.println("Sorry, unable to find " + filename);
		return;
	    }

	    // load a properties file from class path, inside static method
	    prop.load(input);
	    String tempString = null;
	    
	    if ( ( tempString = prop.getProperty(apiKey_KEY)) != null) { this.APIKEY = tempString; }
	    if ( ( tempString = prop.getProperty(dataSrcDomain_KEY)) != null) { this.DATASRC_DOMAIN = tempString; }
	    if ( ( tempString = prop.getProperty(dataSrcCtxUri_KEY)) != null) { this.DATASRC_CTX_URL = tempString; }
	    if ( ( tempString = prop.getProperty(dataSrcUri_KEY)) != null) { this.DATASRC_SRV_URL = tempString; }

	} catch (IOException ex) {
	    ex.printStackTrace();
	} finally {
	    if (input != null) {
		try {
		    input.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    /**
     * A helper method for populating a request from a map of name/value pairs.
     * 
     * @param webResource		the WebTarget to popiulate
     * @param nameValuePairs		values to be set in the query string
     * @return					the webResource with query params
     * 
     */
    public static WebTarget setRequestQueryParams(WebTarget webResource, Map<String, String> nameValuePairs) {
	for (String k : nameValuePairs.keySet() ) webResource = webResource.queryParam(k, nameValuePairs.get(k) );
	return webResource;
    }
    
    /**
     * Sort the keys of a map alphabetically.
     * 
     * @param params	a Map with string keys
     * @return			a sorted List of keys
     */
    public static List<String> sortParamNames(Map<String, String> params) {
	List<String> list = new ImmutableList.Builder<String>().addAll(params.keySet().iterator()).build();
	list.sort(new Comparator<String>() {
	    public int compare(String a, String b)  { return a.toUpperCase().compareTo(b.toUpperCase()); }
	});
	return list;
    }

}

