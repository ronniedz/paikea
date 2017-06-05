package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import cab.bean.srvcs.tube4kids.core.Genre;

public class GenreDAO extends AbstractDAO<Genre> {
    public GenreDAO(SessionFactory factory) {
	super(factory);
    }

    /**
     * Returns a Genre if successful <code>null </code> otherwise.
     * @param genre
     * @return
     * 		A genre with <code>id</code>
     */
    public Genre create(Genre genre) {
	return persist(genre);
    }

    public Long quickCreate(Genre genre) {
	return (Long) currentSession().save(genre);
    }
    public Object create(Genre genre, boolean identOnly) {
	return identOnly ? this.quickCreate(genre) : this.create(genre);
    }
    /**
     * Returns a Genre if successful <code>null</code> otherwise.
     *
     * @param id
     * @return
     * 		A genre.
     */
    public Genre retrieve(Long id) {
	return get(id);
    }

    public Optional<Genre> findById(Long id) {
	return Optional.ofNullable(get(id));
    }

    /**
     * Return all Genre
     * @return
     * 		A List of genre.
     */
    public List<Genre> findAll() {
	return list(namedQuery("cab.bean.srvcs.tube4kids.core.Genre.findAll"));
    }

    /**
     * Return a Genre on successful update. Return null if update fails.
     *
     * @param genre - must include the ID of the genre whose properties will be updated.
     *
     * @return
     * 		A genre.
     */
    public Genre update(Genre genre) {
	Session session = currentSession();
	Genre g = session.get(Genre.class, genre.getId());
	if (g != null) {
	    g = (Genre) session.merge(genre);
	}
	return g;
    }

    /**
     * Return a Genre on successful delete. Return null if delete fails.
     *
     * @param id - the ID of the genre to delete
     *
     * @return
     * 		A genre or null if not deleted.
     */
    public Genre delete(Long id) {
	Genre o = get(id);
	if (o != null) {
	    currentSession().delete(o);
	}
	return o;
    }
}
