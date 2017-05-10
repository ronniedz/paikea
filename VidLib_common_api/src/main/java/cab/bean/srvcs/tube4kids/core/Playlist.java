package cab.bean.srvcs.tube4kids.core;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
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
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "playlist")
@NamedQueries({
        @NamedQuery(
                name = "cab.bean.srvcs.tube4kids.core.Playlist.findAll",
                query = "SELECT p FROM Playlist p"
        ),
        
        @NamedQuery(
            	name = "cab.bean.srvcs.tube4kids.core.Playlist.findUserLists",
            	query = "SELECT p FROM Playlist p where p.user = :user"
        	)
})
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(exclude= {"reviewers", "user"})
@NoArgsConstructor
@EqualsAndHashCode(of  = {"id"})
@Data
public class Playlist implements Comparable<Playlist>{
 
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;

    // ------------------ JOINS ----------------------- //
    @ManyToMany( fetch = FetchType.LAZY, targetEntity = Video.class, cascade = CascadeType.ALL )
    @JoinTable(name = "playlist_video",
    	joinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id"),
    	inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "video_id"))
    private Set<Video> videos;
    
    //@Transient
    @JsonIgnore
    @ManyToMany(
	    mappedBy = "playlists",
	    targetEntity = Child.class,
	    fetch = FetchType.LAZY, 
	    cascade = CascadeType.ALL
    )
    private Set<Child> reviewers;

    @Override
    public int compareTo(Playlist o) {
	
	if ( o == null ) return -1;
	
	Playlist other = (Playlist) o;
	
	return (this.id == null)
		? ( other.getId() == null) ? 0 : -1
		:  ( other.getId() == null) ? 1 : this.id.compareTo(other.getId());
    }

}
