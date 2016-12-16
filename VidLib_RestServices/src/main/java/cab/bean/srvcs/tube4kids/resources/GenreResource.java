package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

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
public class GenreResource {

//    @Context
//    private UriInfo uriInfo;

    final UriBuilder uriBuilder = UriBuilder.fromResource(GenreResource.class);

    private final GenreDAO genreDAO;

    public GenreResource(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }

    /** Create **/
    @POST
    @UnitOfWork
    public Response createGenre(Genre genre) {
	
	Genre nuGenre = genreDAO.create(genre);
	
	Response.Status status = (nuGenre == null)
		? Response.Status.CREATED
		:  Response.Status.NOT_ACCEPTABLE;
	
	return Response.seeOther(uriBuilder.build()).status(status).build(); // .entity(nuGenre)
    }

    /** Retrieve **/
    @GET
    @UnitOfWork
    public List<Genre> listGenres() {
        return genreDAO.findAll();
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
	Genre genre = null;
	
	ResponseBuilder rb = Response.seeOther(uriBuilder.path(genreDat.getId().toString()).build());
	
	if ( (genre = genreDAO.update(genreDat)) == null) {
	    rb.status(Response.Status.NOT_FOUND);
	} else {
	    rb.status(Response.Status.CREATED).entity(genre);
	}
	return rb.build();
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
