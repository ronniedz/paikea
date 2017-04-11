package cab.bean.srvcs.tube4kids.core;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "child")
@NamedQueries({
    @NamedQuery(
	    name = "cab.bean.srvcs.tube4kids.core.Child.findAll",
	    query = "SELECT p FROM Child p"
    )
})
@EqualsAndHashCode(exclude= {"playlists", "parent"} )
@Data
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Playlist.class, cascade= CascadeType.ALL)
    @JoinTable(
	name = "child_playlist",
	joinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"),
	inverseJoinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    )
    private Set<Playlist> playlists;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age_group_id", nullable = false)
    private Long ageGroupId;

    @JsonProperty(value="guardian", access=JsonProperty.Access.READ_ONLY)
    public Map<String, Object> getGuardian() {
	Map<String, Object> guardianInfo = new HashMap<String, Object>();
	if (this.parent != null) {
	    guardianInfo.put("parent_id", parent.getId());
	    guardianInfo.put("parent_name", parent.getName());
	}
	return guardianInfo; 
    }
    
    @JsonIgnore
    @ManyToOne(optional=false, cascade= {CascadeType.ALL}, fetch=FetchType.LAZY)
    private User parent;

    @Column(name = "created", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;

//    public void addPlaylist(Playlist pl) {
//	if (this.playlists == null) {
//	    this.playlists = new HashSet<Playlist>();
//	}
//	playlists.add(pl);
//    }
}
