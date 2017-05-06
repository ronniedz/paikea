package cab.bean.srvcs.tube4kids.core;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user")
@NamedQueries({
        @NamedQuery(
                name = "cab.bean.srvcs.tube4kids.core.User.findAll",
                query = "SELECT p FROM User p"
        )
})
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(exclude= {"playlists"})
@EqualsAndHashCode(exclude= {
	"password", "roles", "activated", "lastLogin", "resetPasswordCode", "activatedAt",
	"createdAt", "updatedAt", "activationCode", "children", "playlists"
})
@Data
public class User implements Principal {
    
    @ManyToMany( fetch = FetchType.EAGER, targetEntity = Role.class, cascade = CascadeType.ALL )
    @JoinTable(name = "user_role",
	joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
	inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<Role>();

    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Playlist> playlists = new HashSet<Playlist>();
    
    @OneToMany(mappedBy="guardian", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Child> children = new HashSet<Child>();

    @JsonIgnore
    @Transient
    @OneToOne(mappedBy="user")
    private Token token;

    @Column(nullable = false)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean activated;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(name = "last_login", nullable = true)
    private Timestamp lastLogin;

    @Column(name = "reset_password_code", nullable = true)
    private String resetPasswordCode;

    @Column(name = "activated_at", nullable = true)
    private Timestamp activatedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, updatable = true, insertable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    @Column(name = "activation_code", nullable = true)
    private String activationCode;

    public User() { }

    public User(String name) {
	this();
	this.email = name;
    }

    public User(String name, Set<Role> roles) {
	this.email = name;
	this.roles.addAll(roles);
    }

    public String getName() {
	return this.email;
    }

    public String getFullName() {
	return this.getFirstname() + " " + this.getLastname();
    }

    public boolean addRole(Role role) {
	return roles.add(role);
    }
    
    public boolean hasAnyRole(String... rolenames) {
	 List<String> needles = Arrays.asList(rolenames);
	return (this.roles.stream().filter(straw -> needles.contains(straw.getName())).count() > 0 );
    }
    
    public boolean hasRole(String role) {
	return roles.contains(new Role(role));
    }

    public boolean isMyPlaylist(Long pidVal) {
	return this.playlists.stream().filter(playlist -> pidVal.equals(playlist.getId())).count() > 0;
    }

    public boolean isMyPlaylist(Playlist needls) {
//	return needls.getUser().getId() == this.getId(); 
//	return this.playlists.contains(needls);
	return isMyPlaylist(needls.getId());
    }
    
    public boolean isMyChild(Long cid) {
	return this.children.stream().filter(child -> cid.equals(child.getId())).count() > 0;
    }
    
    public boolean isMyChild(Child needls) {
//	return needls.getGuardian().getId() == this.getId(); 
//	return this.children.contains(needls);
	return isMyChild(needls.getId());
    }
}

