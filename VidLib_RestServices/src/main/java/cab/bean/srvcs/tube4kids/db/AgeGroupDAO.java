package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.AgeGroup;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class AgeGroupDAO extends AbstractDAO<AgeGroup> {
    public AgeGroupDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<AgeGroup> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public AgeGroup create(AgeGroup ageGroup) {
        return persist(ageGroup);
    }
    
    public Boolean delete(Long id) {
	Boolean rep = Boolean.FALSE;
	AgeGroup o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	    rep = Boolean.TRUE;
	}
	return rep;
    }

    public List<AgeGroup> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.AgeGroup.findAll"));
    }
}
