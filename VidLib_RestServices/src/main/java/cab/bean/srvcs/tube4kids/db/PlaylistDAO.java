package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
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
    
    public Playlist delete(Long id) {
    	Playlist o = get(id);
    	if ( o != null ) {
    	    currentSession().delete(o);
    	}
    	return o;
    }

    public List<Playlist> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findAll"));
    }

    public List<Playlist> findUserLists(Long userId) {
        return list(
        		namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findUserLists").setParameter("userId", userId)
        	);
    }

    
    public Playlist retrieve(Long id) {
	return get(id);
    }

    public Playlist update(Playlist objectData) {
	Session session = currentSession();
	Playlist o = get(objectData.getId());
	if ( o != null ) {
	    o = (Playlist) session.merge(objectData);
	}
	return o;
    }

}
