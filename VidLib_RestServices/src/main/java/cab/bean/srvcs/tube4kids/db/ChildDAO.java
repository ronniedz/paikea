package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cab.bean.srvcs.tube4kids.core.Child;
import cab.bean.srvcs.tube4kids.core.Playlist;
import cab.bean.srvcs.tube4kids.core.User;

public class ChildDAO extends AbstractDAO<Child> {

    public ChildDAO(SessionFactory factory) {
	super(factory);
    }

    public Child create(Child child) {
	return persist(child);
    }

    public Child update(Child objectData) {
	Child o = get(objectData.getId());
	if (o != null) {
	    try {
		currentSession().saveOrUpdate(update(objectData, o));
	    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
		e.printStackTrace();
	    }
	}
	return o;
    }

    public Child update(Child objectData, Child o)
	    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	for (Map.Entry<String, String> entry : BeanUtils.describe(objectData)
		.entrySet()) {
	    if (StringUtils.isNotBlank(entry.getValue())) {
		BeanUtils.setProperty(o, entry.getKey(), entry.getValue());
	    }
	}
	return o;
    }

    public Long quickCreate(Child child) {
	return (Long) currentSession().save(child);
    }

    public Child delete(Child o) {
	delete(o.getId());
	return o;
    }

    public Child delete(Long id) {
	Child o = get(id);
	if (o != null) {
	    Session sess = currentSession();
	    User u = o.getGuardian();
	    if (u != null) {
		u.getChildren().remove(o);
		sess.update(u);
	    }
	    sess.delete(o);
	}
	return o;
    }

    @SuppressWarnings("unchecked")
    public List<Child> findChildrenOf(User user) {
	return list(namedQuery("cab.bean.srvcs.tube4kids.core.Child.findByGuardian").setParameter("user", user));
    }

    @SuppressWarnings("unchecked")
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
