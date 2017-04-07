package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.List;
import java.util.Optional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import cab.bean.srvcs.tube4kids.core.Role;

public class RoleDAO extends AbstractDAO<Role> {
    
    public RoleDAO(SessionFactory factory) {
	super(factory);
    }

    /**
     * Returns a Role if successful <code>null </code> otherwise.
     * @param role
     * @return
     * 		A role with <code>id</code>
     */
    public Role create(Role role) {
	return persist(role);
    }

    public Long quickCreate(Role role) {
	return (Long) currentSession().save(role);
    }
    public Object create(Role role, boolean identOnly) {
	return identOnly ? this.quickCreate(role) : this.create(role);
    }
    /**
     * Returns a Role if successful <code>null</code> otherwise.
     * 
     * @param id
     * @return
     * 		A role.
     */
    public Role retrieve(Long id) {
	return get(id);
    }

    public Optional<Role> findByName(String name) {
	return Optional.ofNullable(uniqueResult(
	    criteria().add(Restrictions.eq("name", name))
	));
    }

    /**
     * Return all Role
     * @return
     * 		A List of role.
     */
    public List<Role> findAll() {
	return list(namedQuery("cab.bean.srvcs.tube4kids.core.Role.findAll"));
    }

    /**
     * Return a Role on successful update. Return null if update fails.
     * 
     * @param role - must include the ID of the role whose properties will be updated.
     *  
     * @return
     * 		A role.
     */
    public Role update(Role role) {
	Session session = currentSession();
	Role g = session.get(Role.class, role.getId());
	if (g != null) {
	    g = (Role) session.merge(role);
	}
	return g;
    }

    /**
     * Return a Role on successful delete. Return null if delete fails.
     * 
     * @param id - the ID of the role to delete 
     *  
     * @return
     * 		A role or null if not deleted.
     */
    public Role delete(Long id) {
	Role o = get(id);
	if (o != null) {
	    currentSession().delete(o);
	}
	return o;
    }
}
