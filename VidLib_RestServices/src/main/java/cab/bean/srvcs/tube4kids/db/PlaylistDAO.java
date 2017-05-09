package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
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
    	User u = o.getUser();
    	
    	if (u !=null) { 
    	    u.getPlaylists().remove(o);
    	    sess.update(u);
    	}

//        	if ( o.getReviewers() != null && ! o.getReviewers().isEmpty()) {
//        	    
//        	    o.getReviewers()
//        	     .stream()
//        	     .filter( ch -> ch.getPlaylists().remove(o))
//        	     .map( ch -> {
//        		 sess.update(ch);
//        		 return ch;
//        	     });
//        	}
        	
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

    public List<Playlist> findUserLists(User user) {
        return list(
        		namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findUserLists").setParameter("user", user)
        	);
    }
    
    public Playlist retrieve(Long id) {
	return get(id);
    }

    public Playlist update(Playlist objectData) {
	Playlist o = get(objectData.getId());
	if (o != null) {
	    try {
		currentSession().saveOrUpdate(update(objectData, o));
	    } catch (IllegalAccessException | InvocationTargetException
		    | NoSuchMethodException e) {
		e.printStackTrace();
	    }
	}

	return o;
    }
     
    public Playlist update(Playlist objectData, Playlist o)
	    throws IllegalAccessException, InvocationTargetException,
	    NoSuchMethodException {
	BeanUtils
		.describe(objectData)
		.entrySet()
		.stream()
		.filter((entry) -> {
		    return !(entry.getValue() == null || entry.getValue()
			    .trim().equals(""));
		})
		.forEach(
			entry -> {
			    try {
				BeanUtils.setProperty(o, entry.getKey(),
					entry.getValue());
			    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			});
	return o;
    }

    public Set<Playlist> loadUserPlaylists(User user) {
	currentSession().load(user, user.getId());
	user.getPlaylists().size();
	return user.getPlaylists();
    }

    
}
