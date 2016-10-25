package cab.bean.srvcs.tube4kids.core;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.JoinColumn;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "genre")
@NamedQueries({
        @NamedQuery(
                name = "cab.bean.srvcs.tube4kids.core.Genre.findAll",
                query = "SELECT p FROM Genre p"
        )
})
@EqualsAndHashCode(exclude = { "videoGenres", "videos", "created" })
@Data
public class Genre {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "created", nullable = false)
    private Timestamp created;
    
    @Column(name = "genre_id", nullable = true)
    private Long genreId;

    // ------------------ JOINS ----------------------- //
//  @Transient
//  @ManyToMany(fetch=FetchType.LAZY)
//    @JoinTable(
//        name = "video_genre",
//        joinColumns = @JoinColumn(name = "genre_id"),
//        inverseJoinColumns = @JoinColumn(name = "video_id")
//    )
//    private List<RelVideo> videos = new ArrayList<RelVideo>();
    
    @JsonIgnore
    @OneToMany(
	    cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	    mappedBy = "genreId",
	    targetEntity = cab.bean.srvcs.tube4kids.core.VideoGenre.class
	    )
    @Cascade({
	org.hibernate.annotations.CascadeType.SAVE_UPDATE, 
	org.hibernate.annotations.CascadeType.DELETE_ORPHAN
    })
    private List<VideoGenre> videoGenres = new ArrayList<VideoGenre>();

}
