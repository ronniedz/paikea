package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import cab.bean.srvcs.tube4kids.core.User;
import cab.bean.srvcs.tube4kids.api.Token;

import java.util.Optional;
import java.util.UUID;

public class TokenDAO extends AbstractDAO<Token> {
    private UserDAO userDAO;

    public TokenDAO(SessionFactory sessionFactory, UserDAO userDAO) {
	super(sessionFactory);
	this.userDAO = userDAO;
    }

    public Token findOrCreateTokenForUser(Long userId) {
	Optional<User> foundUser = userDAO.findById(userId);
	Optional<Token> token = Optional.empty();

	if (foundUser.isPresent()) {
	    User user = foundUser.get();
	    token = findTokenForUser(user);

	    if (!token.isPresent()) {
		Token model = new Token(userId, null);
		model.setUserId(userId);
		model.setToken(UUID.randomUUID());
		return persist(model);
	    }
	}

	return token.orElse(null);
    }

    public Optional<User> findUserWithToken(String subject) {
//	User 
// Criteria criteria = criteria().createAlias("user", "u") .add(Restrictions.eq("u.id", 1L));
//	return Optional.ofNullable(uniqueResult(criteria));
//	Criteria criteria = criteria()
//		.createAlias("user", "u").add(Restriction.eq("u.id", 1L));
//		.setFetchMode('parent.child', FetchMode.JOIN);
//
//	criteria.add(Restrictions.eq("status", "Escalate To");
//
//	.createAlias("mate", "mt", Criteria.LEFT_JOIN, Restrictions.like("mt.name", "good%") )
//	.addOrder(Order.asc("mt.age"))
	
//	return Optional.ofNullable(uniqueResult(criteria));
	return userDAO.findById(1L);
    }
    
    public Optional<Token> findTokenForUser(User user) {
	Criteria criteria = criteria().createAlias("user", "u").add(
		Restrictions.eq("u.id", user.getId()));
	
	return Optional.ofNullable(uniqueResult(criteria));
    }

}
