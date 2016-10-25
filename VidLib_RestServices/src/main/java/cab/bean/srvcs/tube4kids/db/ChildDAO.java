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

    public Optional<Child> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Child create(Child child) {
        return persist(child);
    }

    public Long quickCreate(Child child) {
	return (Long) super.currentSession().save(child);
    }

    public List<Child> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Child.findAll"));
    }
}
