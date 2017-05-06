package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;
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

    public Child update(Child objectData) {
	Child o = get(objectData.getId());
	 try {
	     currentSession().saveOrUpdate(update(objectData, o));
	} catch (IllegalAccessException | InvocationTargetException
		| NoSuchMethodException e) {
	    e.printStackTrace();
	}
	return o;
    }
    
    public Child update(Child objectData, Child o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	    BeanUtils.describe(objectData).entrySet().stream().filter(( entry ) -> {
	        return ! (entry.getValue() == null || entry.getValue().trim().equals(""));
	    } )
	    .forEach(entry -> {
	       try {
		BeanUtils.setProperty(o, entry.getKey(), entry.getValue());
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } 
	    });
	return o;
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
