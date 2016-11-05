package cab.bean.srvcs.tube4kids.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.NonNull;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "video")
@NamedQueries({ @NamedQuery(
	name = "cab.bean.srvcs.tube4kids.core.RelVideo.findAll",
	query = "SELECT vid FROM RelVideo vid")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class RelVideo extends BasicVideo {

    protected DateTime publishedAt; 	// snippetType.publishedAt
    protected Long userId; 			// used in MySQL
    
    protected User user; 			// used for joining videoGenre PK
    
    private Set<Playlist> playlists;
    private List<VideoGenre> videoGenres = new ArrayList<VideoGenre>();


    @NonNull
    @Id
    @Column(name = "video_id", unique = true, nullable = false)
    public String getVideoId() {
        return videoId;
    }

    @JsonProperty
    @Column(name = "user_id", nullable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long user_id) {
	this.userId = user_id;
    }

    @Column(name = "etag", unique = true, nullable = false)
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @JsonProperty
    @Column(name = "published_at", unique = true, nullable = false)
    public DateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(DateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    @JsonProperty
    @Column(name = "title", unique = true, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    @Column(name = "default_thumbnail")
    public String getDefaultThumbnail() {
        return defaultThumbnail;
    }

    public void setDefaultThumbnail(String defaultThumbnail) {
        this.defaultThumbnail = defaultThumbnail;
    }

    @Override
    public String toString() {
	return "Video [ etag=" + etag + ", videoId=" + videoId
		+ ", publishedAt=" + publishedAt + ", title=" + title
		+ ", description=" + description + ", defaultThumbnail="
		+ defaultThumbnail + "]";
    }


	// ------------------  JOINS ----------------------- //
	
    	@JsonIgnore
	@Transient
	@ManyToMany(
		mappedBy="videos",
		targetEntity=cab.bean.srvcs.tube4kids.core.Playlist.class
	)
	public Set<Playlist>getPlaylists() {
	    return playlists;
	}


//
//	@JsonIgnore
//	@ManyToMany(
//		mappedBy = "videos",
//		targetEntity=cab.bean.srvcs.tube4kids.core.Genre.class
//	)
//	private Set<Genre> genres = new HashSet<Genre>();

        
    	@JsonProperty
	@OneToMany(
		fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		mappedBy = "pk.video"
	)
	@org.hibernate.annotations.Cascade({
	    org.hibernate.annotations.CascadeType.SAVE_UPDATE, 
	    org.hibernate.annotations.CascadeType.DELETE_ORPHAN
	})
	public List<VideoGenre> getVideoGenres() {
	    return videoGenres;
	}

	public void setVideoGenres(List<VideoGenre> videoGenres) {
//	    videoGenres.forEach( g -> {
//		g.getPk().
//	    });
	    this.videoGenres = videoGenres;
	}

	public void setPlaylists(Set<Playlist> playlists) {
	    this.playlists = playlists;
	}

    	@JsonIgnore
	public void setUser(User user) {
	    this.user = user;
	    this.userId = user.getId();
	}

//	public void setGenres(Set<Genre> genres) {
//	    this.genres = genres;
//	}
	
	
}
