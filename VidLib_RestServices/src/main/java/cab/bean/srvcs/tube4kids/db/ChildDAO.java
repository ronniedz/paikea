package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.Child;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class ChildDAO extends AbstractDAO<Child> {
    public ChildDAO(SessionFactory factory) {
	super(factory);
    }

    public Child create(Child child) {
	return persist(child);
    }

    public Long quickCreate(Child child) {
	return (Long) super.currentSession().save(child);
    }
    
    public Boolean delete(Long id) {
	Boolean rep = Boolean.FALSE;
	Child o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	    rep = Boolean.TRUE;
	}
	return rep;
    }

    public List<Child> findAll() {
	return list(namedQuery("cab.bean.srvcs.tube4kids.core.Child.findAll"));
    }

    public Optional<Child> findById(Long id) {
	return Optional.ofNullable(get(id));
    }
}
