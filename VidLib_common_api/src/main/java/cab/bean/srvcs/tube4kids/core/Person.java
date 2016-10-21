package cab.bean.srvcs.tube4kids.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "people")
@NamedQueries({
        @NamedQuery(
                name = "cab.bean.srvcs.tube4kids.core.Person.findAll",
                query = "SELECT p FROM Person p"
        )
})
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "jobTitle", nullable = false)
    private String jobTitle;

    public Person() {
    }

    public Person(String fullName, String jobTitle) {
        this.setFullName(fullName);
        this.jobTitle = jobTitle;
    }
    
    public Person(String firstName, String lastName, String jobTitle) {
    	this.firstName = firstName;
    	this.lastName = lastName;
    	this.jobTitle = jobTitle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return  this.firstName + " " +  this.lastName;
    }

    public String getFirstName() {
    	return  this.firstName;
    }
    
    public String getLastName() {
    		return this.lastName;
    }
    
    public void setFullName(String fullName) {
    		String[] str = fullName.split(" ");
    		this.firstName = str[0];
    		this.lastName = str[1];
    }
    
    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }

        final Person that = (Person) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.jobTitle, that.jobTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, this.firstName + " " +  this.lastName, jobTitle);
    }
}
