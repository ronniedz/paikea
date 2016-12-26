package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.Video;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

public class VideoDAO extends AbstractDAO<Video> {

    public VideoDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Video> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public Video create(Video video) {
        return persist(video);
    }
    
    public Video delete(String id) {
	Video o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	}
	return o;
    }

    public List<Video> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Video.findAll"));
    }

    public String addVideoYTVideo(Video video) {
	return (String) super.currentSession().save(video);
    }

    public Object create(Video video, boolean identOnly) {
	// TODO Auto-generated method stub
	return identOnly ? currentSession().save(video) : persist(video);
    }

}

