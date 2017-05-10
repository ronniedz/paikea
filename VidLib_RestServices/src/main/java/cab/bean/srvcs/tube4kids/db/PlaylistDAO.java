package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.User;

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

	if (o != null) {
	    Session sess = currentSession();
	    User u = o.getUser();

	    if (u != null) {
		u.getPlaylists().remove(o);
		sess.update(u);
	    }
	    if (o.getVideos() != null) o.getVideos().clear();
	    try {
		sess.delete(o);
	    } catch (org.hibernate.ObjectDeletedException oe) {
		oe.printStackTrace();
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
	    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
		e.printStackTrace();
	    }
	}
	return o;
    }
     
    public Playlist update(Playlist objectData, Playlist o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	for (Map.Entry<String, String> entry : BeanUtils.describe(objectData)
		.entrySet()) {
	    if (StringUtils.isNotBlank(entry.getValue())) {
		BeanUtils.setProperty(o, entry.getKey(), entry.getValue());
	    }
	}
	return o;
    }

    public Set<Playlist> loadUserPlaylists(User user) {
	currentSession().load(user, user.getId());
	user.getPlaylists().size();
	return user.getPlaylists();
    }
}
