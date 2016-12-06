package cab.bean.srvcs.tube4kids;

import javax.ws.rs.core.Response;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.api.YouTubeResponse;
import cab.bean.srvcs.tube4kids.resources.Config;
import cab.bean.srvcs.tube4kids.resources.YouTubeAgent;
import cab.bean.srvcs.tube4kids.resources.YouTubeAgentImpl;

/**
 * Unit test for simple App.
 */
public class YouTubeAgentTest extends TestCase {
//    private static Log LOGGER = LogFactory.getLog(YouTubeAgentTest.class);
    private static Logger LOGGER = LoggerFactory.getLogger(YouTubeAgentTest.class);

//    private YouTubeAgent yta = null;
    private Config cnf;
    

    /**
     * @param testName name of the test case
     */
    public YouTubeAgentTest( String testName )
    {
        super( testName );
	cnf = new Config();
//	yta = new YouTubeAgentImpl();

    }

    public static Test suite()
    {
        return new TestSuite(YouTubeAgentTest.class);
    }

    public void testSearchQuery() {
	LOGGER.info("runSearchQuery test Query");
	Response reply = (new YouTubeAgentImpl(Config.PropKey.APIKEY.getValue())).runSearchQuery(cnf.testSearchParams);
	try {
	    assertEquals(Response.Status.OK.getStatusCode(), reply.getStatus());
	    YouTubeResponse tq = (YouTubeResponse) reply.getEntity();
	    assertNull(tq.getError());
	} catch (java.lang.ClassCastException jlce) {
	    LOGGER.error(jlce.getMessage());
	}
    }
    
    public void testVideoDetailsQuery() {
	LOGGER.info("runSearchQuery test Query");
	Response reply = (new YouTubeAgentImpl(Config.PropKey.APIKEY.getValue())).runVideoDetailsQuery(cnf.testDetailLookupParams);
	try {
	    assertEquals(Response.Status.OK.getStatusCode(), reply.getStatus());
	    YouTubeResponse tq = (YouTubeResponse) reply.getEntity();
	    assertNull(tq.getError());
	} catch (java.lang.ClassCastException jlce) {
	    LOGGER.error(jlce.getMessage());
	}
    }
    
    public void testMultiQuery() {
	YouTubeAgent agn = new YouTubeAgentImpl(Config.PropKey.APIKEY.getValue());
	LOGGER.info("runSearchQuery test Multi Query-1");
	Response reply = agn.runSearchQuery(cnf.testSearchParams);
	try {
	    assertEquals(Response.Status.OK.getStatusCode(), reply.getStatus());
	    YouTubeResponse tq = (YouTubeResponse) reply.getEntity();
	    assertNotNull(tq.getItems());
	    assertNotNull(tq.getEtag());
	    assertNull(tq.getError());
	    LOGGER.info("runSearchQuery test Multi Query-2");
	    agn.runSearchQuery(cnf.testSearchParams.xput("pageToken", tq.getNextPageToken()));
	    assertEquals(Response.Status.OK.getStatusCode(), reply.getStatus());
	    tq = (YouTubeResponse) reply.getEntity();
	    assertNotNull(tq.getItems());
	    assertNotNull(tq.getEtag());
	    assertNull(tq.getError());
	    
	} catch (java.lang.ClassCastException jlce) {
	    LOGGER.error(jlce.getMessage());
	}
    }

    /**
     *  Invalid Key test
     *  
     *  Youtube  response looks like:
     *  
     *  	domain=usageLimits, reason=keyInvalid, message=Bad Request)]))
     */
    public void testInvalidKey()
    {
	LOGGER.info("runSearchQuery test Invalid Key");
	YouTubeAgent agn = new YouTubeAgentImpl();
	agn.setApiKey((String) null);
	Response reply = agn.runSearchQuery( cnf.testSearchParams.xput("key", Config.PropKey.APIKEY.getValue() +1) );
	try {
	    // Confirm  http-response-code is as expected
	    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), reply.getStatus());
	    
	    YouTubeResponse tq = (YouTubeResponse) reply.getEntity();
	    
	    // Confirm errors were mapped
	    assertNotNull("Error not mapped from YouTube repsonse.", tq.getError());
	    assertNotNull("Error code not mapped.", tq.getError().code());
	    
	    // Check reason message
	    assertEquals("keyInvalid", tq.getError().errors().get(0).getReason());
	    LOGGER.info("q.getError().code(): " + tq.getError().code());
	    
	    // Check status code
	    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), tq.getError().code());
	} catch (java.lang.ClassCastException jlce) {
	    LOGGER.error(jlce.getMessage());
	}
   }
    
    /**
     *  Invalid Key test
     *  
     *  Youtube  response looks like:
     *  
     */
    public void testNoKey()
    {
	LOGGER.info("runSearchQuery test No Key");
	YouTubeAgent agn = new YouTubeAgentImpl();
	agn.setApiKey((String) null);
	Response reply = agn.runSearchQuery( cnf.testSearchParams );
	try {
	    // Check HTTP status code
	    assertEquals(Response.Status.FORBIDDEN.getStatusCode(), reply.getStatus());
	    
	    YouTubeResponse tq = (YouTubeResponse) reply.getEntity();
	    
	    // Confirm errors were mapped
	    assertNotNull("Error object expected but not found.", tq.getError());
	    // Check Mapped error code
	    assertEquals(Response.Status.FORBIDDEN.getStatusCode(), tq.getError().code());

	} catch (java.lang.ClassCastException jlce) {
	    LOGGER.error(jlce.getMessage());
	}
    }
}

