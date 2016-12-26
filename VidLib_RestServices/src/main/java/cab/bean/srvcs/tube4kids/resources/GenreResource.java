package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;

import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import cab.bean.srvcs.tube4kids.resources.ResourceStandards.ResponseData;

@Path("/genre")
@Produces(MediaType.APPLICATION_JSON)
public class GenreResource extends BaseResource {

    private final GenreDAO genreDAO;

    public GenreResource(GenreDAO genreDAO) {
	super();
        this.genreDAO = genreDAO;
    }

    /** Create **/
    @POST
    @UnitOfWork
    public Response createGenre(Genre genre) {
	Object o = genreDAO.create(genre, isMinimalRequest());

	boolean respBody =  (o != null);
	ResponseData dat = new ResponseData()
		.setSuccess(respBody)
		.setEntity(o);

        return doPOST(dat).build();
    }

    /** Retrieve **/
    @GET
    @UnitOfWork
    public Response listGenres() {
	List<Genre> list = genreDAO.findAll();
	return doGET(new ResponseData(list).setSuccess(list != null)).build();
    }
    
    /** Update **/
    @Path("/{id: [0-9]+}")
    @PATCH
    @UnitOfWork
    public Response updateGenre(@PathParam("id") Long id, Genre genreDat) {
	genreDat.setId(id);
	return updateGenre(genreDat);
    }
    
    /** Update **/
    @PATCH
    @UnitOfWork
    public Response updateGenre(Genre objectData) {

	Genre o = genreDAO.update(objectData);
	
	boolean respBody =  (o != null);
	ResponseData dat = new ResponseData()
		.setSuccess(respBody)
		.setEntity( respBody ? isMinimalRequest() ? o.getId() : o : null);
	
	if ( o != null) {
	    dat.setLocation( uriBuilder.path(o.getId().toString()).build() );
	}

        return doPATCH(dat).build();
    }
    
    /** Delete **/
    @Path("/{id: [0-9]+}")
    @DELETE
    @UnitOfWork
    public Response deleteGenre(@PathParam("id") Long id) {
	
	Genre g = genreDAO.delete(id);

	ResponseData dat = new ResponseData().setSuccess(g != null);

	if (! isMinimalRequest()) {
		dat.setEntity(g);
	}
        return doDELETE(dat).build();
    }
    
    /** Delete **/
    @Path("/{id: [0-9]+}")
    @GET
    @UnitOfWork
    public Response viewGenre(@PathParam("id") Long id) {
	
	Genre g = genreDAO.retrieve(id);
	
	ResponseData dat = new ResponseData().setSuccess(g != null);
	
	if (! isMinimalRequest()) {
	    dat.setEntity(g);
	}
        return doGET(dat).build();
    }

}
