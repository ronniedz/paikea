package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;
import cab.bean.srvcs.tube4kids.core.User;
import com.mysql.cj.api.x.Collection;

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

    private Optional<PropertyDescriptor> findGetterOf(String propName) {
	try {
	   return
		   Arrays.asList(Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors())
		   	.stream().filter(propDesc -> { return propDesc.getName().equals(propName); } )
		   	.findFirst();

	} catch (IntrospectionException e) {
	    e.printStackTrace();
	}
	return Optional.<PropertyDescriptor>empty();
    }

    public User loadAssets(User user, String[] properties) {

	currentSession().load(user, user.getId());
	try {
	    Optional<PropertyDescriptor> desc = null;

	    Object o = null;
	    for (String propertiy : properties) {
            	desc = findGetterOf(propertiy);
            	if (desc.isPresent()) {
            	    PropertyDescriptor pd = desc.get();
            	    o = pd.getReadMethod().invoke(user);
            	    if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
            		((Collection) o ).count();
            	    }
            	}
	    }
	}
	catch (IllegalAccessException | IllegalArgumentException
		| InvocationTargetException e) {
	    e.printStackTrace();
	}
	return user;
    }

    public User loadRoles(User user) {
	currentSession().load(user, user.getId());
	user.getRoles().size();
	return user;
    }

    public User loadPlaylists(User user) {
	currentSession().load(user, user.getId());
	user.getPlaylists().size();
	return user;
    }

    public User loadChildren(User user) {
	currentSession().load(user, user.getId());
	user.getRoles().size();
	return user;
    }
}
