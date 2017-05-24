package cab.bean.srvcs.pipes.processor;

import java.io.InputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import cab.bean.srvcs.pipes.PersistenceHelper;
import cab.bean.srvcs.pipes.model.VideoSearchRequest;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converts the in.message.body into a  {@link YouTubeResponse} object.
 * Forwards in.headers to the out message.
 *
 * @author ronalddennison
 *
 */
public class Http4Processor implements Processor {

    private final ObjectMapper objectMapper;

    public Http4Processor(ObjectMapper objectMapper) {
	super();
	this.objectMapper = objectMapper;
    }

/**
 * Camel will store the HTTP response from the external server on the OUT body. All headers from the IN message will be copied
 * to the OUT message, so headers are preserved during routing. Additionally Camel will add the HTTP response headers as well
 * to the OUT message headers.
 */
    public void process(Exchange exchange) throws Exception {
	final Message IN = exchange.getIn();
	final Message OUT = exchange.getOut();

	YouTubeResponse ytResp = objectMapper.readValue((InputStream)IN.getBody(), YouTubeResponse.class);

	// TODO confirm if needed in pipe
	VideoSearchRequest ytReq = IN.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, VideoSearchRequest.class);
   	exchange.getOut().setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, ytReq);

	OUT.setHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME, IN.getHeader(PersistenceHelper.HDR_QUERYPARAMS_NAME));
	OUT.setHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA, IN.getHeader(PersistenceHelper.HDR_NAME_SERVICE_DEST_DATA));
	OUT.setHeader(PersistenceHelper.HDR_FOUNDQUERY, IN.getHeader(PersistenceHelper.HDR_FOUNDQUERY));

	String collectionName = IN.getHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, String.class);
	ytResp.setCollectionName(collectionName);
	OUT.setHeader(PersistenceHelper.HDR_NAME_COLLECTION_NAME, collectionName);
	OUT.setBody(ytResp);
    }
}
