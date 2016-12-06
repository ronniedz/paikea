/**
 * 
 */
package cab.bean.srvcs.tube4kids.core;

import java.util.List;

import javax.persistence.Embedded;

import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author ronalddennison
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public abstract class BasicVideo implements VideoType {

    protected String etag;
    protected String videoId;
    protected String title;				// snippetType.title
    protected String description;		// snippetType.description
    protected String defaultThumbnail;

    //@-JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    //@-JsonProperty
    //protected String publishedAt; // snippetType.publishedAt

    public String publishedAt; //   snippet.publishedAt
    
    @NonNull
    @JsonProperty
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @JsonProperty
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public String getDefaultThumbnail() {
        return defaultThumbnail;
    }

    public void setDefaultThumbnail(String defaultThumbnail) {
        this.defaultThumbnail = defaultThumbnail;
    }
    
}

