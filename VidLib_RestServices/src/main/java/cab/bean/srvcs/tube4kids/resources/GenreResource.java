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

@Path("/genre")
@Produces(MediaType.APPLICATION_JSON)
public class GenreResource extends ResourceStandards {

//    @Context
//    private UriInfo uriInfo;


    private final GenreDAO genreDAO;

    public GenreResource(GenreDAO genreDAO) {
	super();
        this.genreDAO = genreDAO;
    }

    /** Create **/
    @POST
    @UnitOfWork
    public Response createGenre(Genre genre) {
	
	ResponseData dat = new ResourceStandards.ResponseData();
	
	Genre nuGenre = genreDAO.create(genre);

	if (nuGenre != null ) { 
	    dat.isSuccess = true;

	    dat.setStatus(Response.Status.CREATED);
	    if (isMinimalRequest()) {
		dat.setEntity(nuGenre.getId());
	    }
	    else {
		dat.setEntity(nuGenre);
	    }
	} else {
	    dat.setErrorMessage("Genre " + Response.Status.NOT_FOUND.toString());
	    dat.setStatus(Response.Status.NOT_FOUND);
	}
	
	return reply(dat);
    }

    /** Retrieve **/
    @GET
    @UnitOfWork
    public Response listGenres() {
	List<Genre> list = genreDAO.findAll();
	ResponseData dat = new ResourceStandards.ResponseData();
	if (list != null) {
	    dat.isSuccess = true;
	    dat.setEntity(list);
	}
	return reply(dat);
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
    public Response updateGenre(Genre genreDat) {
	
	ResponseData dat = new ResourceStandards.ResponseData();
	
	Genre entity = genreDAO.update(genreDat);
	
	if ( (entity = genreDAO.update(genreDat)) == null) {
	    
	    dat.setEntity(genreDat);
	    dat.setErrorMessage("Genre " + Response.Status.NOT_FOUND.toString());
	    dat.setStatus(Response.Status.NOT_FOUND);
	    
	} else {
	    dat.setLocation( uriBuilder.path(entity.getId().toString()).build() );
	    dat.isSuccess = true;

	    if ( isMinimalRequest()) {
//		dat.setEntity(entity.getId());
		dat.setStatus(Response.Status.NO_CONTENT);
	    }
	    else {
		dat.setEntity(entity);
		dat.setStatus(Response.Status.CREATED);
	    }
	}

	return super.reply(dat);
    }
    
    /** Delete **/
    @Path("/{id: [0-9]+}")
    @DELETE
    @UnitOfWork
    public Response deleteGenre(@PathParam("id") Long id) {

	Response.Status stat = genreDAO.delete(id)
		? Response.Status.NO_CONTENT
		:  Response.Status.NOT_FOUND;
	
	return Response.seeOther(uriBuilder.build()).status(stat).build();
    }

}
