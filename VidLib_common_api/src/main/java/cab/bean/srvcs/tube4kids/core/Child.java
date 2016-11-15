package cab.bean.srvcs.tube4kids.core;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

import lombok.Data;

@Entity
@Table(name = "child")
@NamedQueries({
    @NamedQuery(
	    name = "cab.bean.srvcs.tube4kids.core.Child.findAll",
	    query = "SELECT p FROM Child p"
    )
})
@Data
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany(targetEntity = cab.bean.srvcs.tube4kids.core.Playlist.class)
    @JoinTable(
	name = "child_playlist",
	joinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id"),
	inverseJoinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id")
    )
    private Set<Playlist> playlists;

    public Set<Playlist> getPlaylists() {
	return playlists;
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age_group_id", nullable = false)
    private Long ageGroupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created", nullable = false)
    private Timestamp created;
}
