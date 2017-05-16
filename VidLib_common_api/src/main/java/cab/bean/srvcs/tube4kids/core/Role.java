package cab.bean.srvcs.tube4kids.core;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "role")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"name"})
public class Role  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnore
    @Transient
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles", targetEntity = User.class)
    private Set<User> users = new HashSet<User>();

    public Role(String name) {
	this.name=name;
    }
}

