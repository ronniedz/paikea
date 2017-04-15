package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;

import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.Playlist;

public class ChildDAO extends AbstractDAO<Child> {

    public ChildDAO(SessionFactory factory) {
	super(factory);
    }

    public Child create(Child child) {
	return persist(child);
    }

    public Long quickCreate(Child child) {
	return (Long) currentSession().save(child);
    }
    
    public Child delete(Child o) {
	currentSession().delete(o);
	return o;
    }
    
    public Child delete(Long id) {
	Child o = get(id);
	return delete(o);
    }

    public List<Child> findAll() {
	return list(namedQuery("cab.bean.srvcs.tube4kids.core.Child.findAll"));
    }

    public Optional<Child> findById(Long id) {
	return Optional.ofNullable(get(id));
    }


    public Optional<Child> findByIdLoadPlaylists(Long id) {
	Child child = get(id);
	if (child !=null) { child.getPlaylists().size(); }
	return Optional.ofNullable(child);
    }
    
    public Object create(Child aChild,  boolean identOnly) {
	return identOnly ? currentSession().save(aChild) : this.create(aChild);
    }

    public Set<Playlist> getPlaylistOf(Child child) {
	return get(child.getId()).getPlaylists();
    }
}
