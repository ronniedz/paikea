package cab.bean.srvcs.tube4kids.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.annotations.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "genre")
@NamedQueries({
        @NamedQuery(
                name = "cab.bean.srvcs.tube4kids.core.Genre.findAll",
                query = "SELECT p FROM Genre p"
        )
})
@Data
@EqualsAndHashCode(exclude = { "videoGenres", "created" })
public class Genre {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created;

    @Column(name = "genre_id", nullable = true)
    private Long genreId;

    // ------------------ JOINS ----------------------- //
    @SuppressWarnings("deprecation")
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
