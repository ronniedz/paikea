package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.Genre;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class GenreDAO extends AbstractDAO<Genre> {
    public GenreDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Genre> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Genre create(Genre genre) {
        return persist(genre);
    }
    
   public Boolean delete(Long id) {
	Boolean rep = Boolean.FALSE;
	Genre o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	    rep = Boolean.TRUE;
	}
	return rep;
   }

    public List<Genre> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Genre.findAll"));
    }
}
