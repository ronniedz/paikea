package cab.bean.srvcs.tube4kids.resources;

import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/genre")
@Produces(MediaType.APPLICATION_JSON)
public class GenreResource {

    private final GenreDAO genreDAO;

    public GenreResource(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
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
