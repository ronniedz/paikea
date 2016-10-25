package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.RelVideo;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public List<Playlist> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findAll"));
    }

    public List<Playlist> findUserLists(Long userId) {
        return list(
        		namedQuery("cab.bean.srvcs.tube4kids.core.Playlist.findUserLists").setParameter("userId", userId)
        	);
    }

}
