package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User> {
    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public User create(User user) {
        return persist(user);
    }
    
    public Boolean delete(Long id) {
	Boolean rep = Boolean.FALSE;
	User o = get(id);
	if ( o != null ) {
	    currentSession().delete(o);
	    rep = Boolean.TRUE;
	}
	return rep;
    }

    public List<User> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.User.findAll"));
    }
}
