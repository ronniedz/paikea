package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.RelVideo;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

public class VideoDAO extends AbstractDAO<RelVideo> {

    public VideoDAO(SessionFactory factory) {
	
        super(factory);
    }

    public Optional<RelVideo> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public RelVideo create(RelVideo video) {
        return persist(video);
    }
    
    public Boolean delete(String id) {
	Boolean rep = Boolean.FALSE;
	RelVideo o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	    rep = Boolean.TRUE;
	}
	return rep;
    }

    public List<RelVideo> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.RelVideo.findAll"));
    }

    public String addVideoYTVideo(RelVideo video) {
	return (String) super.currentSession().save(video);
    }

}

