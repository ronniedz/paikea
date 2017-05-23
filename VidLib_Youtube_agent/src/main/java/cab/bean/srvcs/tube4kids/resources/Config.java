package cab.bean.srvcs.tube4kids.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.client.WebTarget;

import jersey.repackaged.com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ronalddennison
 *
 */
public class Config {

    public enum PropKey {
	DATASRC_DOMAIN("dataSrcDomain",	"https://www.googleapis.com"),
	DATASRC_CTX_URI("dataSrcCtxUri", 	"/youtube/v3"),
	DATASRC_SEARCH_SRV_URI("dataSrcSearchUri", "/search"),
	DATASRC_DETAILS_SRV_URI("dataSrcDetailsUri",	 "/videos"),
	APIKEY("apiKey", null);

	private final String keyName;
	private String defaultValue;

	PropKey(final String name, final String value) {
	    this.keyName = name;
	    this.defaultValue = value;
	}

	public String getKey() {
	    return keyName;
	}

	public String getValue() {
	    return defaultValue;
	}

	public PropKey setValue(String value) {
	    this.defaultValue = value;
	    return this;
	}

	public String toString() {
	    return getKey();
	}

    }

    String filename = "config.properties";

    // public static final String HDR_NAME_SERVICE_DEST_DATA = "X-dbstore_data";
    // public static final String HDR_NAME_SERVICE_DEST =
    // "X-ServiceDestination";
    public static final String HDR_VALUE_SERVICE_DEST = "youTubeAgent";

    /**
     * Test parameters of a YT query.
     * 
     * <pre>
     *             part=snippet
     *             maxResults=10
     *             order=date
     *             publishedAfter=2006-01-01T00%3A00%3A00Z
     *             q=filmes+completo+dublado+-legendado
     *             regionCode=BR
     *             relevanceLanguage=PT
     *             type=video
     *             videoCaption=any
     *             videoDuration=long
     *             videoLicense=youtube
     *             videoType=any
     *             fields=etag%2Citems%2Ckind%2CnextPageToken%2CpageInfo
     *             key=AIzaSyAmOXO8tcUauYkj1POSnEzle_Rm61LAOe
     * </pre>
     */
    public final QVal testSearchParams = new QVal()
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

    
    
// https://www.googleapis.com/youtube/v3/videos?
//    id=QH4DhHpYEIc,4o1TFIIciAg
//    &type=video
//    &key=AIzaSyAmOXO8tcUauYkj1POSnEzle_Rm61LAOes
//    &part=contentDetails,snippet
    
    public final QVal testDetailLookupParams = new QVal()
    .xput("id", "QH4DhHpYEIc,4o1TFIIciAg")
    .xput("part", "contentDetails,snippet")
    ;
    
    /**
     * 
     */
    public Config() {
	super();
	loadSettings();
    }

    private void loadSettings() {

	InputStream input = null;

	try {
	    if (null != (input = Config.class.getResourceAsStream(filename))) {
		Properties prop = new Properties();
		// load a properties file from class path, inside static method
		prop.load(input);
//		String tempString = null;

		if (StringUtils.isNotBlank(prop.getProperty(PropKey.APIKEY.keyName))) {

		    for (PropKey pk : PropKey.values()) {
			pk.setValue(prop.getProperty(pk.keyName, pk.defaultValue));
		    }		    
//
//		    if ((tempString = prop.getProperty(dataSrcDomain_KEY)) != null)
//			this.DATASRC_DOMAIN = tempString;
//		    if ((tempString = prop.getProperty(dataSrcCtxUri_KEY)) != null)
//			this.DATASRC_CTX_URI = tempString;
//		    if ((tempString = prop.getProperty(dataSrcUri_KEY)) != null)
//			this.DATASRC_SEARCH_SRV_URI = tempString;
		} else {
			throw new RuntimeException(" '" + PropKey.APIKEY +"' required.");
		}
		
	    } else {
		throw new RuntimeException("Sorry, unable to find " + filename);
	    }
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
     * @param webResource
     *            the WebTarget to popiulate
     * @param nameValuePairs
     *            values to be set in the query string
     * @return the webResource with query params
     * 
     */
    public static WebTarget setRequestQueryParams(WebTarget webResource, Map<String, String> nameValuePairs) {
	for (Map.Entry<String,String> entry : nameValuePairs.entrySet())
	    webResource = webResource.queryParam(entry.getKey(), entry.getValue());
	return webResource;
    }

    /**
     * Sort the keys of a map alphabetically.
     * 
     * @param params
     *            a Map with string keys
     * @return a sorted List of keys
     */
    public static List<String> sortParamNames(Map<String, String> params) {
	List<String> list = new ImmutableList.Builder<String>().addAll(params.keySet().iterator()).build();
	list.sort(new Comparator<String>() {
	    public int compare(String a, String b) {
		return a.toUpperCase().compareTo(b.toUpperCase());
	    }
	});
	return list;
    }

}
