package cab.bean.srvcs.tube4kids.resources;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

public class Config {

    public static final String APIKEY = "AIzaSyAmOXO8tcUauYkj1POSnEzle_Rm61LAOes";
    
    public static final String DATASRC_DOMAIN = "https://www.googleapis.com";
    public static final String DATASRC_CTX_URL = "/youtube/v3";
    public static final String DATASRC_SRV_URL = "/search";

//    public static final String HDR_NAME_SERVICE_DEST_DATA = "X-dbstore_data";
//    public static final String HDR_NAME_SERVICE_DEST = "X-ServiceDestination";
    public static final String HDR_VALUE_SERVICE_DEST = "youTubeAgent";
    
    public static final QVal  testParams = new QVal()
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
     
     */

    public static WebTarget setRequestQueryParams(WebTarget webResource, Map<String, String> params) {
	for (String k : params.keySet() )  webResource = webResource.queryParam(k, params.get(k) );
	return webResource;
    }
    
    public static List<String> sortParamNames(Map<String, String> params) {
	List<String> list = new LinkedList<String>(params.keySet());
	list.sort(new Comparator<String>() {
	    public int compare(String a, String b)  { return a.toUpperCase().compareTo(b.toUpperCase()); }
	});
	return list;
    }

}

