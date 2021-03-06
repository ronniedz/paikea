package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;

import java.util.Map;
import java.util.Optional;

public class TokenDAO extends AbstractDAO<Token> {

    public TokenDAO(SessionFactory sessionFactory) {
	super(sessionFactory);
    }

    public Optional<Token> findBySubjectAndIssuer(String subject, String issuer) {
	Criteria criteria = criteria()
		.add(Restrictions.eq("subject", subject))
		.add(Restrictions.like("idp", issuer));
//	return Optional.ofNullable(namedQuery("cab.bean.srvcs.tube4kids.core.Token.findBySubject").setParameter("subject", subject).uniqueResult());
	return Optional.ofNullable(uniqueResult(criteria));
    }


    public Optional<Token> findBySubject(String subject) {
	Criteria criteria = criteria()
		.add(Restrictions.eq("subject", subject));
	return Optional.ofNullable(uniqueResult(criteria));
    }


    public Optional<User> findUserByActiveSubject(String subject) {
	Criteria criteria = criteria()
		.add(Restrictions.eq("active", true))
		.add(Restrictions.eq("subject", subject));

	Token token = uniqueResult(criteria);
	User user = null;
	if (token != null ) {
	    user = token.getUser();
	    if (user != null) {
		user.getRoles().size();
		user.getPlaylists().size();
		user.getChildren().size();
	    }
	}
	return Optional.ofNullable(user);
    }


    public Token create(Token beanToken) {
        return persist(beanToken);
    }

    public User findUserBySubject(String subject) {
	Optional<Token> t = findBySubject(subject);

	return t.isPresent() ? t.get().getUser() : null;
    }

    public Optional<Token> locateSubject(Map<String, Object> userValues) {
	Criteria criteria = criteria().createAlias("user", "user");
	Criterion r1 = Restrictions.eq("subject", userValues.get("ubject"));
	Criterion r2 = Restrictions.eq("user.email", userValues.get("email"));
	Criterion r3 = Restrictions.and(Restrictions.eq("user.firstname", userValues.get("firstname")) , Restrictions.eq("user.lastname", userValues.get("lastname")));
	return Optional.ofNullable(uniqueResult(criteria.add(Restrictions.or( r1, r2, r3 ))));
    }


}
