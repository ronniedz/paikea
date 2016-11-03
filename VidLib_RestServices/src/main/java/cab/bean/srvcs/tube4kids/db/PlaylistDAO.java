package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import cab.bean.srvcs.tube4kids.core.Playlist;

public class PlaylistDAO extends AbstractDAO<Playlist> {
    
    public PlaylistDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Playlist> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Playlist create(Playlist playlist) {
        return persist(playlist);
    }
    
    public Boolean delete(Long id) {
    	Boolean rep = Boolean.FALSE;
    	Playlist o = get(id);
    	if ( o != null ) {
    	    currentSession().delete(o);
    	    rep = Boolean.TRUE;
    	}
    	return rep;
    }

    public List<Playlist> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findAll"));
    }

    public List<Playlist> findUserLists(Long userId) {
        return list(
        		namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findUserLists").setParameter("userId", userId)
        	);
    }

}
