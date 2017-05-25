package cab.bean.srvcs.tube4kids.core;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "child")
@NamedQueries({
    @NamedQuery(
	    name = "cab.bean.srvcs.tube4kids.core.Child.findAll",
	    query = "SELECT p FROM Child p"
    )
})
@EqualsAndHashCode(exclude= {"playlists", "guardian"} )
@ToString(of= {"id"})
@Data
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Playlist.class)
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
    public Map<String, Object> getParentInfo() {
	Map<String, Object> guardianInfo = new HashMap<String, Object>();
	if (this.guardian != null) {
	    guardianInfo.put("guardian_id", guardian.getId());
	    guardianInfo.put("guardian_name",  guardian.getFullName());
	}
	return guardianInfo;
    }

    @JsonIgnore
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private User guardian;

    @Column(name = "created", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;

}
