package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableList;

import java.util.List;

@Path("/genre")
@Produces(MediaType.APPLICATION_JSON)
public class GenreResource {

    private final GenreDAO genreDAO;

    public GenreResource(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }
    
    @Path("/{id}")
    @DELETE
    @UnitOfWork
    public Response deleteGenre(@PathParam("id") Long id) {
    	genreDAO.delete(id);
	return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @UnitOfWork
    public Genre createGenre(Genre genre) {
        return genreDAO.create(genre);
    }

    @GET
    @UnitOfWork
    public List<Genre> listGenres() {
        return genreDAO.findAll();
    }

}
