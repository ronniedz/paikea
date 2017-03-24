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

    public Optional<Token> findBySubject(String subject) {
	Criteria criteria = criteria().add(Restrictions.eq("subject", subject));
//	return Optional.ofNullable(namedQuery("cab.bean.srvcs.tube4kids.core.Token.findBySubject").setParameter("subject", subject).uniqueResult());
	return Optional.ofNullable(uniqueResult(criteria));
    }

    public Token create(Token beanToken) {
        return persist(beanToken);
    }

}
