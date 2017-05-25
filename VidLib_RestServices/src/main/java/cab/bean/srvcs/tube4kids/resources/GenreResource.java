package cab.bean.srvcs.tube4kids.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.PATCH;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import cab.bean.srvcs.tube4kids.auth.RoleNames;
import cab.bean.srvcs.tube4kids.core.Genre;
import cab.bean.srvcs.tube4kids.db.GenreDAO;

@Path("/genre")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.CONTENT_MODERATOR_ROLE, RoleNames.UI_MANAGER_ROLE})
public class GenreResource extends BaseResource {

    private final GenreDAO genreDAO;

    public GenreResource(GenreDAO genreDAO) {
	super();
        this.genreDAO = genreDAO;
    }

    /** Create **/
    @POST
    @RolesAllowed({RoleNames.ADMIN_ROLE, RoleNames.GUARDIAN_ROLE, RoleNames.UI_MANAGER_ROLE})
    @UnitOfWork
    public Response createGenre(Genre genre) {
	Genre o = genreDAO.create(genre);

	ResponseData dat = new ResponseData()
	   .setSuccess(o != null)
	   .setEntity(isMinimalRequest() ? o.getId() : o );
	if ( o != null) {
	    dat.setLocation(
	    	UriBuilder.fromResource(this.getClass())
	    	   .path(o.getId().toString()
	    	   ).build()
	    	);
	}

        return doPOST(dat).build();
    }

    /** Retrieve **/
    @GET
    @UnitOfWork
    @PermitAll
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

	ResponseData dat = new ResponseData()
		.setSuccess(o != null)
		.setEntity(isMinimalRequest() ? null : o );

	if ( o != null) {
	    dat.setLocation( UriBuilder.fromResource(this.getClass()).path(o.getId().toString()).build() );
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
	dat.setEntity(isMinimalRequest() ? null : g );
        return doDELETE(dat).build();
    }

    /** Delete **/
    @Path("/{id: [0-9]+}")
    @GET
    @UnitOfWork
    @PermitAll
    public Response viewGenre(@PathParam("id") Long id) {
	Genre g = genreDAO.retrieve(id);
	ResponseData dat = new ResponseData().setSuccess(g != null);
	dat.setEntity(g);
        return doGET(dat).build();
    }

}
