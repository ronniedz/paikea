package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.User;

import java.util.stream.Collectors;

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
    	Session sess = currentSession();
    	User u = o.getCreator();
    	
    	if (u !=null) { 
    	    u.getPlaylists().remove(o);
    	    sess.update(u);
    	}

        	if ( o.getReviewers() != null && ! o.getReviewers().isEmpty()) {
        	    
        	    o.getReviewers()
        	     .stream()
        	     .filter( ch -> ch.getPlaylists().remove(o))
        	     .map( ch -> {
        		 sess.update(ch);
        		 return ch;
        	     });
        	}
        	
        	if (o.getVideos() != null) o.getVideos().clear();

	    try {
		sess.delete(o);
	    } catch (org.hibernate.ObjectDeletedException oe) {
		System.err.println("DeletedException Message: "
			+ oe.getMessage());
		System.err.println("DeletedException Cause: " + oe.getCause());
	    }
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
