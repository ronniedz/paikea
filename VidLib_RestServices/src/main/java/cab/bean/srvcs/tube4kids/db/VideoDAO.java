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

    public List<RelVideo> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.RelVideo.findAll"));
    }

    public String addVideoYTVideo(RelVideo video) {
	System.out.println("RelVideo: " + video);
	return (String) super.currentSession().save(video);
    }
//    public RelVideo addVideoYTVideo(RelVideo video) {
//	System.out.println("RelVideo: " + video);
//	return (RelVideo) get(super.currentSession().save(video));
//    }
}
