package cab.bean.srvcs.tube4kids.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import cab.bean.srvcs.tube4kids.resources.utils.RoleHelper;

/**
 * Adds a predictable structure to responses for Rest verbs.
 * The "standard" is adapted from:
 * 	- http://www.restapitutorial.com/lessons/httpmethods.html
 * 	- https://greenbytes.de/tech/webdav/rfc7240.html
 * 
 * In short:
 *<pre>
 * 	GET		->	ok: 200 || err: 400/404			- 	content	/  np-loc
 * 
 * 	POST	->	ok: 200, 204 || err: 400/409		-    ? content	/  Location
 * 	PUT		->	ok: 200, 204 || err: 404			-    ? content	/  Location  
 * 	PATCH	->	ok: 200, 204 || err: 404, 409,412	-    ? content	/  Content-Location
 * 	DELETE	->	ok: 200, 204  || err: 404, 409,412	-    ? content	/  Location
 * 		
 * 	-: stipulate form of response using 'Prefer' header.
 * 
 * 	Eg:
 * 		
 * 		1.	`Prefer: return=representation`
 * 
 * 		2.	`Prefer: return=minimal`
 * 		
</pre>
 * @author ronalddennison
 *
 */
public abstract class ResourceStandards {

    @Context
    protected UriInfo uriInfo;

    @Context
    protected javax.ws.rs.core.Request request;


    @Context
    protected javax.ws.rs.core.HttpHeaders headers;
    
//    protected UriBuilder uriBuilder;

    
    protected ResourceStandards() {
//	this.uriBuilder = UriBuilder.fromResource(this.getClass());
    }

    /**
     * Default status response:<pre>
     * 		Success:
     * 			- 201 (CREATED) if an entity is set (via setEntity(object))
     * 			- 204 (NO_CONTENT if response body is empty
     * 		Error:
     * 			- 400 (BAD_REQUEST), This is a deviation from the "norm" which prescribes a 404 error. However, 404 is unlikely in a POST.
     * </pre>
     * @param
     * 		respData
     * @return
     * 		a responseBuilder
     */
//    protected ResponseBuilder doPOST(ResponseData respData, String redirStr) {
//
//	boolean isOk = respData.success;
//	ResponseBuilder rb = Response
//		.status(isOk
//			? respData.hasEntity() ? Response.Status.CREATED : Response.Status.NO_CONTENT
//				: Response.Status.BAD_REQUEST);
//	
//	if (respData.success &&  ! respData.hasLocation()) {
//	    rb.header("Location", UriBuilder.fromResource(this.getClass()).build().toString());
//	}
//	
//	
//	parseRespData( respData, rb);
//	return rb;
//    }
    
    protected ResponseBuilder doPOST(ResponseData respData) {
	boolean isOk = respData.success;
	ResponseBuilder rb = Response
		.status(isOk
			? respData.hasEntity() ? Response.Status.CREATED : Response.Status.NO_CONTENT
			: Response.Status.BAD_REQUEST);

	if (respData.success &&  ! respData.hasLocation()) {
	    rb.header("Location", UriBuilder.fromResource(this.getClass()).build().toString());
	}


	parseRespData( respData, rb);
	return rb;
    }

    /**
     * Default status response:<pre>
     * 		Success:
     * 			- 200 (OK)
     * 		Error:
     * 			- 404 (NOT_FOUND)
     * </pre>
     * @param
     * 		respData
     * @return
     * 		a responseBuilder
     */
    protected ResponseBuilder doGET(ResponseData respData) {
	ResponseBuilder rb = Response.status(
		respData.success
            		?Response.Status.OK
            		: Response.Status.NOT_FOUND
        );

	if (! respData.success) {
	    rb.header("Location", respData.location);
	}

	parseRespData(respData, rb);
	return rb;
    }

    /**
     * Default status response:<pre>
     * 		Success:
     * 			- 200 (OK) if an entity is set (via setEntity(object))
     * 			- 204 (NO_CONTENT if response body is empty
     * 		Error:
     * 			- 404 (NOT_FOUND)
     * </pre>
     * @param
     * 		respData
     * @return
     * 		a responseBuilder
     */
    protected ResponseBuilder doPUT(ResponseData respData) {
	ResponseBuilder rb = Response
		.status(respData.success
			? respData.hasEntity() ? Response.Status.OK : Response.Status.NO_CONTENT
			: Response.Status.NOT_FOUND);
	
	if (! respData.success) {
	    rb.header("Location", respData.location);
	}

	parseRespData( respData, rb);
	return rb;
    }

    /**
     * Default status response:<pre>
     * 		Success:
     * 			- 200 (OK) if an entity is set (via setEntity(object))
     * 			- 204 (NO_CONTENT) if response body is empty
     * 
     * 		Error:
     * 			- 404 (NOT_FOUND)
     * </pre>
     * @param
     * 		respData
     * @return
     * 		a responseBuilder
     */
    protected ResponseBuilder doPATCH(ResponseData respData) {
	
	ResponseBuilder rb = Response
		.status(respData.success
			? respData.hasEntity() ? Response.Status.OK : Response.Status.NO_CONTENT
			: Response.Status.NOT_FOUND);
	
	if (! respData.success) {
	    rb.header("Location", UriBuilder.fromResource(this.getClass()).build().toString());
	}

	parseRespData( respData,  rb);
	return rb;
    }

    /**
     * Default status response:<pre>
     * 		Success:
     * 			- 204 (NO_CONTENT)
     * 		Error:
     * 			- 404 (NOT_FOUND)
     * </pre>
     * @param
     * 		respData
     * @return
     * 		a responseBuilder
     */
    protected ResponseBuilder doDELETE(ResponseData respData) {
	ResponseBuilder rb = Response.status(
		respData.success
            		? respData.hasEntity() ? Response.Status.OK : Response.Status.NO_CONTENT
            		:  Response.Status.NOT_FOUND
        );
	
	rb.header("Location", UriBuilder.fromResource(this.getClass()).build().toString());
	parseRespData( respData, rb);
	return rb;
    }
    
    public ResponseBuilder reply(ResponseData dat) {
	ResponseBuilder r =null;
	//methodResponseFuncs.get(method).apply(dat);
	    String instanceMethod = request.getMethod().toUpperCase();

	switch(instanceMethod) {
        		case "POST" : {
        		    r = doPOST(dat);
        		}
        		break;
        		case "GET" : {
        		    r = doGET(dat);
        		}
        		break;
        		case "PUT" : {
        		    r = doPUT(dat);
        		}
        		break;
        		case "PATCH" : {
        		    r = doPATCH(dat);
        		}
        		break;
        		case "DELETE" : {
        		    r = doDELETE(dat);
        		}
        		break;
	}
	return r;
    }


    private void parseRespData(ResponseData respData,
	    ResponseBuilder rb) {

	if (respData.hasStatus()) {
	    rb.status(respData.status);
	}
	
	if (respData.hasEntity()) {
	    rb.entity(respData.entity);
	} else if (respData.hasErrorMessage()) {
	    rb.entity(respData.errorMessage);
	}
	
	if (respData.hasLocation()) {
	    rb.header("Location", respData.location);
	}
    }

    private final String full = "representation";	// return=
    private final String minimal = "minimal"; 	// return=
    
    protected boolean isMinimalRequest() {
	String preftype = headers.getHeaderString("Prefer".toLowerCase());
	return (preftype != null)
		? preftype.contains(minimal) ? true : false
		: false;
    }


    class ResponseData {

	boolean success = false;
	String errorMessage = null;
	String location = null;
	Object entity = null;
	Response.Status status = null;

	public ResponseData() {}
	
	public ResponseData(Object entity) {
	    this();
	    this.entity = entity;
	}

	public ResponseData(Object entity, String location) {
	    this(entity);
	    this.location = location;
	}
	
	public ResponseData(Object entity, String location, Response.Status status) {
	    this(entity, location);
	    this.entity = status;
	}
	
	public ResponseData(Object entity, String location, Response.Status status, String errorMessage) {
	    this(entity, location, status);
	    this.errorMessage = errorMessage;
	}

	public ResponseData(Object entity, URI location, Response.Status status, String errorMessage) {
	    this(entity, location.toString(), status, errorMessage);
	}

	boolean hasStatus() {
	    return status != null;
	}

	boolean hasErrorMessage() {
	    return StringUtils.isNotEmpty(errorMessage);
	}

	boolean hasEntity() {
	    return entity != null;
	}

	boolean hasLocation() {
	    return StringUtils.isNotEmpty(location);
	}

	public ResponseData setSuccess(boolean flag) {
	    this.success = flag;
	    return this;
	}
	
	public ResponseData setErrorMessage(String errorMessage) {
	    this.errorMessage = errorMessage;
	    return this;
	}

	public ResponseData setLocation(String location) {
	    this.location = location;
	    return this;
	}
	
	public ResponseData setLocation(URI location) {
	    this.location = location.toString();
	    return this;
	}

	public ResponseData setEntity(Object entity) {
	    
	    // Jersey-Jackson oversight of String in JSON fix.
	    this.entity = (entity != null && String.class.equals(entity.getClass())) ? "\"" + entity + "\"" : entity;
	    return this;
	}

	public ResponseData setStatus(Response.Status status) {
	    this.status = status;
	    return this;
	}
    }

}
