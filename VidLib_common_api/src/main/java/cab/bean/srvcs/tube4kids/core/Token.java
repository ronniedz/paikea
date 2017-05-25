package cab.bean.srvcs.tube4kids.core;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "token")
@NamedQueries({
    @NamedQuery(
            name = "cab.bean.srvcs.tube4kids.core.Token.findBySubject",
            query = "SELECT t FROM Token t where t.subject = :subject"
    ),

})
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@EqualsAndHashCode
public class Token {

    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique=true)
    private String subject;

    @Column
    private String idp;

    @Column(nullable=false)
    private boolean active = false;

    @Column
    private String issuer = "beancab";

    @Column
    private String audience;

    @OneToOne
    private User user;

    public Token() {}

    public Token(User user, String subject) {
	this.user = user;
	this.subject = subject;
    }

    public long getId() {
        return id;
    }

    public Token setId(long id) {
        this.id = id;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Token setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getIssuer() {
        return issuer;
    }

    public Token setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Token setUser(User user) {
        this.user = user;
        return this;
    }

    public String getAudience() {
        return audience;
    }

    public Token setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public String getIdp() {
        return idp;
    }

    public Token setIdp(String idp) {
        this.idp = idp;
        return this;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public Token setActive(boolean active) {
        this.active = active;
        return this;
    }


}