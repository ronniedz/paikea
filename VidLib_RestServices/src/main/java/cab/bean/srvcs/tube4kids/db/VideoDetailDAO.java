package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.Video;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.List;
import java.util.Optional;

public class VideoDetailDAO extends AbstractDAO<Video> {

    public VideoDetailDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Video> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public Video create(Video video) {
        return persist(video);
    }

    public Boolean delete(String id) {
	Boolean rep = Boolean.FALSE;
	Video o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	    rep = Boolean.TRUE;
	}
	return rep;
    }

    @SuppressWarnings("unchecked")
    public List<Video> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Video.findAll"));
    }

    public String addVideoYTVideo(Video video) {
	return (String) super.currentSession().save(video);
    }

}

