package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Function;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

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
    
    protected UriBuilder uriBuilder;

    protected Map<String, Function<ResponseData, ResponseBuilder>> methodResponseFuncs = new HashMap<String, Function<ResponseData, ResponseBuilder>>();

    protected Function<ResponseData, ResponseBuilder> verbGET = (respData) -> {
	ResponseBuilder rb = Response.status(
		respData.isSuccess
            		?Response.Status.OK
            		: Response.Status.BAD_REQUEST
        );

	parseRespData(respData, rb);
	return rb;
    };

    protected Function<ResponseData, ResponseBuilder> verbPOST = (respData) -> {

	boolean isOk = respData.isSuccess;
	ResponseBuilder rb = Response
		.status(isOk ? respData.hasEntity() ? Response.Status.CREATED
			: Response.Status.NO_CONTENT
			: Response.Status.BAD_REQUEST);

	if (isOk &&  ! respData.hasLocation()) {
	    respData.location = uriBuilder.build().toString();
	}
	parseRespData( respData, rb);
	return rb;
    };

    protected Function<ResponseData, ResponseBuilder> verbPATCH = (respData) -> {
	
	boolean isOk = respData.isSuccess;
	ResponseBuilder rb = Response
		.status(isOk ? respData.hasEntity() ? Response.Status.CREATED
			: Response.Status.NO_CONTENT
			: Response.Status.BAD_REQUEST);
	
	if (isOk &&  ! respData.hasLocation()) {
	    respData.location = uriBuilder.build().toString();
	}
	parseRespData( respData, rb);
	return rb;
    };
    
    protected ResourceStandards() {
	this.uriBuilder = UriBuilder.fromResource(this.getClass());
	methodResponseFuncs.put("GET", verbGET);
	methodResponseFuncs.put("POST", verbPOST);
	methodResponseFuncs.put("PATCH", verbPATCH);
    }

    public Response reply(ResponseData dat) {

	String method = request.getMethod();

	ResponseBuilder r = methodResponseFuncs.get(method).apply(dat);
	return r.build();
    }


    private void parseRespData(ResponseData respData,
	    ResponseBuilder rb) {

	if (respData.hasStatus()) {
	    rb.entity(respData.status);
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

	boolean isSuccess = false;
	String errorMessage = null;
	String location = null;
	Object entity = null;
	Response.Status status = null;

	ResponseData() {
	}

	ResponseData(Object entity, String location, Response.Status status, String errorMessage) {
	    this();
	    this.entity = entity;
	    this.errorMessage = errorMessage;
	    this.status = status;
	    this.location = location;
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

	public void setErrorMessage(String errorMessage) {
	    this.errorMessage = errorMessage;
	}

	public void setLocation(String location) {
	    this.location = location;
	}
	
	public void setLocation(URI location) {
	    this.location = location.toString();
	}

	public void setEntity(Object entity) {
	    this.entity = entity;
	}

	public void setStatus(Response.Status status) {
	    this.status = status;
	}


    }
}
