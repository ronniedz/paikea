package cab.bean.srvcs.tube4kids.core;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import lombok.*;

@Entity
@Table(name = "video_genre")
@NamedQueries({
      @NamedQuery(
              name = "cab.bean.srvcs.tube4kids.core.VideoGenre.findAll",
              query = "SELECT p FROM VideoGenre p"
      )
})
@AssociationOverrides({
    @AssociationOverride(name ="pk.user", joinColumns = @JoinColumn(name ="user_id")),
    @AssociationOverride(name ="pk.video", joinColumns = @JoinColumn(name ="video_id"))
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class VideoGenre {
    
    VideoGenrePk pk;

    private Date lastModified = new Date();

    private Long genreId;
    
    private Long genre2Id;

    public VideoGenre() {
	this.pk =  new VideoGenrePk();
    }
    
    public VideoGenre(RelVideo video, User user) {
	this.pk =  new VideoGenrePk(video, user);
    }
    
    public VideoGenre(RelVideo video, User user, Long genreId, Long genre2Id) {
	this.pk =  new VideoGenrePk(video, user);
	this.genreId = genreId;
	this.genre2Id = genre2Id;
    }
    
    public VideoGenre(RelVideo video, User user,
	    ImmutablePair<Long, Long> genreIds) {
	this(video, user);
	this.setGenrePairs(genreIds);
    }

    @JsonIgnore
    @Transient
    public void setGenrePairs(ImmutablePair<Long, Long> genres) {
	this.genreId = genres.left;
	this.genre2Id = genres.right;
    }
    
    @JsonIgnore
    @Transient
    public void setGenrePairs(Long[] genres) {
	this.genreId = genres[0];
	this.genre2Id = genres[1];
    }
    
    @JsonIgnore
    @EmbeddedId
    public VideoGenrePk getPk() {
        return pk;
    }

    public void setPk(VideoGenrePk pk) {
        this.pk = pk;
    }

    @JsonProperty
    @Column(name = "genre_id", nullable = true)
    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }
    
    @JsonProperty
    @Column(name = "genre2_id", nullable = true)
    public Long getGenre2Id() {
        return genre2Id;
    }

    public void setGenre2Id(Long genre2Id) {
        this.genre2Id = genre2Id;
    }

    @JsonIgnore
    @Transient
    public RelVideo getVideo() {
        return getPk().getVideo();
    }

    public void setVideo(RelVideo video) {
	getPk().setVideo(video);
    }

    @JsonIgnore
    @Transient
    public User getUser() {
        return getPk().getUser();
    }

    public void setUser(User user) {
	getPk().setUser(user);
    }

    @JsonProperty
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    
    @Embeddable
    @ToString
    public static class VideoGenrePk implements Serializable {
        private static final long serialVersionUID = 490200883975165432L;
        
        private RelVideo video;
        private User user;

        public VideoGenrePk() {}
        
        public VideoGenrePk(RelVideo video, User user) {
            this.user = user;
            this.video = video;
        }

        @Override
        public boolean equals(Object o) {
    	if ((o != null) && o.getClass().equals(this.getClass())) {
    	    VideoGenrePk  that = ((VideoGenrePk) o);
    	    return
    		    (this.user != null ? that.getUser().equals(this.user) : false) &&
    		    (this.video != null ? that.getVideo().equals(this.video) : false);
    	}
    	return false;
        }

        public int hashCode() {
            return 31 * (
            		((this.user != null) ? this.user.hashCode() : 0)  +
            		((this.video != null) ? this.video.hashCode() : 0)
            	);
        }

	//Transient
        @ManyToOne
        @JoinColumn(name="video_id")
	public RelVideo getVideo() {
	    return video;
	}

	public void setVideo(RelVideo video) {
	    this.video = video;
	}

        @ManyToOne
        @JoinColumn(name="user_id")
	public User getUser() {
	    return user;
	}

	public void setUser(User user) {
	    this.user = user;
	}

    }
    

}
