package cab.bean.srvcs.tube4kids.db;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import cab.bean.srvcs.tube4kids.core.Token;
import cab.bean.srvcs.tube4kids.core.User;

import java.util.Optional;
import java.util.UUID;

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
	return Optional.ofNullable(token != null ? token.getUser() : null);
    }


    public Token create(Token beanToken) {
        return persist(beanToken);
    }

    public User findUserBySubject(String subject) {
	Optional<Token> t = findBySubject(subject);
	
	return t.isPresent() ? t.get().getUser() : null; 
    }

}
