package cab.bean.srvcs.pipes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cab.bean.srvcs.tube4kids.resources.Config;


public class PersistenceHelper {

    public static final String HDR_NAME_COLLECTION_NAME = "X-CollectionName";
    public static final String HDR_NAME_SERVICE_DEST_DATA = "X-dbstore_data";
    public static final String HDR_NAME_SERVICE_DEST = "X-ServiceDestination";

    public static final String prefix = "vcache_";
    public static final String HDR_QUERYPARAMS_NAME = "params";
    public static final String HDR_FOUNDQUERY_NAME = "found";
    public static final String META_DATA_DB_NAME = "cache_control_dat";
    public static final String DEFAULT_FETCH_SIZE = "10";


    private static Pattern matchQuotes =  Pattern.compile("[\"|']");
    private static Pattern regexPhrases =  Pattern.compile("\\s+");

    public static String determineName(Map<String, String> params) {
	String colname = null;

	String rawQuerySorted = normalizeQueryString(params);;

	try {
	    byte[] bytesOfMessage = rawQuerySorted.getBytes(StandardCharsets.UTF_8);
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    String hexed =  hexString(md.digest(bytesOfMessage));

	    
//	    MessageDigest m=MessageDigest.getInstance("MD5");
//	    m.update(rawQuerySorted.getBytes(StandardCharsets.UTF_8),0,rawQuerySorted.length());
//	    tuple.setCollectionName("v_" + (new BigInteger(1,m.digest()).toString(16)).substring(0, 14));

	    colname = hexed.substring(0, 14);
	    
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return PersistenceHelper.prefix + colname;
}

private static String normalizeQueryString(Map<String, String> params) {
	standardizeSearchTerm(params);
/*
∫http://localhost:7070/ix/a/b?
q=punky
&pageToken=CBgQAA
&prevPageToken=
&maxResults=6
&part=snippet
&order=date
&publishedAfter=2006-01-01T00%3A00%3A00Z
&regionCode=BR
&relevanceLanguage=PT
&type=video
&videoCaption=any
&videoLicense=youtube
&videoType=any
&fields=etag%2Citems%2Ckind%2CnextPageToken%2CprevPageToken%2CpageInfo	
 */
	
	String[] relevantParams = new String[] {"regionCode", "relevanceLanguage"} ;
	
	StringBuilder sb = new StringBuilder(makeNameValPairs("q", params));
	for (String pName: relevantParams ) {
	    sb.append("&").append(makeNameValPairs(pName, params));
	}

	return sb.toString();
//	return params.get("q");
}


private static void standardizeSearchTerm(Map<String, String> params) {
	String q = matchQuotes.matcher(params.get("q")).replaceAll("");
	String[] stringary = regexPhrases.split(q.toLowerCase());
	Arrays.sort(stringary);
	for (String cell : stringary ) {
	    // System.out.println(cell);
	}
	
	StringBuilder sb = new StringBuilder();
	for (String cell : stringary ) {
	    sb.append(cell).append(" ");
	}
	
	params.put("q", sb.toString().trim());
}

private static String makeNameValPairs(String key, Map<String, String> params) {
	return key + "=" + java.net.URLEncoder.encode(params.get(key));
}

private static String hexString(byte[] byteData) {
	StringBuffer hexString = new StringBuffer();
	for (int i = 0; i < byteData.length; i++) {
	    String hex = Integer.toHexString(0xff & byteData[i]);
	    if (hex.length() == 1)
		hexString.append('0');
	    hexString.append(hex);
	}
	return hexString.toString();
}
}
