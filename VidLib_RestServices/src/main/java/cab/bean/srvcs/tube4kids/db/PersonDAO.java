package cab.bean.srvcs.tube4kids.db;

import cab.bean.srvcs.tube4kids.core.Person;
import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class PersonDAO extends AbstractDAO<Person> {
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Person create(Person person) {
        return persist(person);
    }

    public Person delete(Person genre) {
	return delete(genre);
    }

    public List<Person> findAll() {
        return list(namedQuery("cab.bean.srvcs.tube4kids.core.Person.findAll"));
    }
}
