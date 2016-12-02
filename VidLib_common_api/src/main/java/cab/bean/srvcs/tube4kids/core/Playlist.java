package cab.bean.srvcs.tube4kids.core;

import java.sql.Timestamp;
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
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "playlist")
@NamedQueries({
        @NamedQuery(
                name = "cab.bean.srvcs.tube4kids.core.Playlist.findAll",
                query = "SELECT p FROM Playlist p"
        ),
        
        @NamedQuery(
            	name = "cab.bean.srvcs.tube4kids.core.Playlist.findUserLists",
            	query = "SELECT p FROM Playlist p where p.userId = :userId"
        	)
})
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@EqualsAndHashCode(exclude= {"reviewers", "creator"})
@Data
public class Playlist {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //JsonProperty(value="creator_id", access=JsonProperty.Access.READ_ONLY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, insertable = false)
    @Transient
    private User creator;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created", nullable = false)
    private Timestamp created;

    // ------------------ JOINS ----------------------- //
    @ManyToMany(targetEntity = cab.bean.srvcs.tube4kids.core.Video.class)
    @JoinTable(name = "playlist_video",
    	joinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id"),
    	inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "video_id"))
    private Set<Video> videos;
    
    @JsonIgnore
    @Transient
    @ManyToMany(mappedBy = "playlists", targetEntity = cab.bean.srvcs.tube4kids.core.Child.class)
    private Set<Child> reviewers;

 

    // ------------------ END JOINS ------------------- //

}
