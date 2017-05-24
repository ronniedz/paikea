package cab.bean.srvcs.pipes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class PersistenceHelper {

    public static final String HDR_NAME_COLLECTION_NAME = "X-CollectionName";
    public static final String HDR_NAME_SERVICE_DEST_DATA = "X-dbstore_data";
    public static final String HDR_NAME_SERVICE_DEST = "X-ServiceDestination";
    public static final String REST_QUERYSTRING = "X-QueryString";

    public static final String prefix = "vcache_";
    public static final String HDR_QUERYPARAMS_NAME = "params";
    public static final String HDR_FOUNDQUERY = "found";
    public static final String META_DATA_DB_NAME = "cache_control_dat";
    public static final String DEFAULT_FETCH_SIZE = "10";

    private static final Pattern matchQuotes = Pattern.compile("[\"|']");
    private static final Pattern regexPhrases = Pattern.compile("\\s+");

    public static String makeNameFromQuery(Map<String, String> params) throws QueueException {
	try {
	    final MessageDigest md = MessageDigest.getInstance("MD5");
	    
	    return PersistenceHelper.prefix +
		    hexString(md.digest(normalizeQueryString(params).getBytes(StandardCharsets.UTF_8)))
		    .substring(0, 14);
	} catch (Exception e) {
	    throw new QueueException(e.getMessage());
	}
    }

    /*
    http://localhost:7070/ix/a/b?
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

    /**
     * Creates a unique string-id for the query. Modifies {@code params}, resetting the {@code 'q'} value to it's alphabetized variant.
     * 
     * @param params a {@code Map} of query values
     * @return a unique string for the given query.
     * @throws UnsupportedEncodingException
     */
    public static String normalizeQueryString(Map<String, String> params) throws UnsupportedEncodingException {
	
	params.put("q", alphabetize(params.get("q")));

	final StringBuilder sb = new StringBuilder();
	
	for (String pName : new String[] { "q", "regionCode", "relevanceLanguage" }) {
	    sb.append("&").append(pName+ "=" + java.net.URLEncoder.encode(params.get(pName), StandardCharsets.UTF_8.toString()));
	}

	return sb.toString();
    }
    
    public static String alphabetize(final String phrase) {
	final String[] stringary = regexPhrases
		.split(matchQuotes.matcher(phrase).replaceAll("").toLowerCase());
	Arrays.sort(stringary);
	return String.join(" ", stringary);
    }
    
    private static String hexString(byte[] byteData) {
	final StringBuffer hexString = new StringBuffer();
	
	for (int i = 0; i < byteData.length; i++) {
	    String hex = Integer.toHexString(0xff & byteData[i]);
	    if (hex.length() == 1) hexString.append('0');
	    hexString.append(hex);
	}
	return hexString.toString();
    }
}
