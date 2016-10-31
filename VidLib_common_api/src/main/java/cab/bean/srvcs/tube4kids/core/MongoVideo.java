package cab.bean.srvcs.tube4kids.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.ws.rs.core.UriBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import cab.bean.srvcs.tube4kids.api.ErrorBox;
import cab.bean.srvcs.tube4kids.api.YouTubeResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoVideo extends BasicVideo {

    private Long id; // Gen ID

    private static final Long[] defaultGIDs = new Long[] {1L, 1L};

    // TODO Add empty genres to the MongoDocument. This will allow us to use Mongo as primary source down the line
    protected List<VideoGenre> videoGenres;
    
    @JsonProperty
    protected String publishedAt; // snippetType.publishedAt

//    private Map<String, String> callback = new HashMap<String, String>();
//    
    public MongoVideo() {
	super();
	this.videoGenres = Arrays.asList(new VideoGenre[] { new VideoGenre().setGenreIds(defaultGIDs) });
	
//	callback.put("open" , "");
//	callback.put("addTo","");
    }
    
    public MongoVideo(String etag) {
	this();
	this.etag = etag;
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    @JsonProperty
    public List<VideoGenre> getVideoGenres() {
	return videoGenres;
    }

    public void setVideoGenres(List<VideoGenre> videoGenres) {
	this.videoGenres = videoGenres;
    }

    @JsonProperty 
    public String getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty 
    public String getEtag() {
        return etag;
    }

    @JsonProperty
     public String getVideoId() {
        return videoId;
    }

    @JsonProperty 
    public String getTitle() {
        return title;
    }

    @JsonProperty 
    public String getDescription() {
        return description;
    }

    @JsonProperty 
    public String getDefaultThumbnail() {
	return this.defaultThumbnail;
    }
    @JsonProperty(value ="id", access=JsonProperty.Access.WRITE_ONLY)
    public void setVidId(Map<String, Object> foo) {
	this.videoId  = (String) foo.get("videoId");
    }
    
    @JsonProperty(value="snippet", access=JsonProperty.Access.WRITE_ONLY)
    public void setSnippet(Map<String, Object> foo) {
	this.publishedAt  = (String) foo.get("publishedAt");
	this.title  = (String) foo.get("title");
	this.description  = (String) foo.get("description");
	this.defaultThumbnail = unpackThumbnail((Map) foo.get("thumbnails"));
    }
 
    private String unpackThumbnail(Map<String, Map<String, Object>> foo) {
	Map<String, Object> dat = foo.get("default");
	String turl = UriBuilder.fromUri(dat.get("url").toString())
		.queryParam("dim", dat.get("width") + ":" + dat.get("height"))
		.build().toString();
	return turl;
    }
    
    // ------------------ JOINS ----------------------- //
    @JsonIgnore
    @Transient
    @ManyToMany(mappedBy = "videos", targetEntity = cab.bean.srvcs.tube4kids.core.Playlist.class)
    private Set<Playlist> playlists;
    
    @JsonIgnore
    @Transient
    public Set<Playlist> getPlaylists() {
	return playlists;
    }

    @Override
    public String toString() {
	return "MongoVideo [ id=" + id + ", videoId=" + videoId
		+  ", title=" + title + ", defaultThumb="
		+ defaultThumbnail + "]";
    }

    public void setDefaultThumbnail(String thumb) {
	this.defaultThumbnail = thumb;    }

    public void setEtag(String etag) {
        this.etag = etag;    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;    }

    public void setTitle(String title) {
        this.title = title;    }

    public void setDescription(String description) {
        this.description = description;    }

//    public Map<String, String> getCallback() {
//        return callback;
//    }
//    
//    public MongoVideo setACallback(String key, String value) {
//	callback.put(key, value);
//	return this;
//    }
    
//    @Transient
//    public  String getACallback(String key) {
//	return callback.get(key);
//    }
    
}
