/**
 * 
 */
package cab.bean.srvcs.tube4kids.core;

import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author ronalddennison
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public abstract class BasicVideo implements AVideo {

    protected String etag;

    protected String videoId;

    //@-JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    //@-JsonProperty
    //protected String publishedAt; // snippetType.publishedAt

    protected String title; // snippetType.title

    protected String description; // snippetType.description

    protected String defaultThumbnail;

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
    

//    public abstract String getEtag();
//    public abstract String getVideoId();
//    public abstract String getPublishedAt();
//    public abstract String getTitle();
//    public abstract String getDescription();
//    public abstract String getDefaultThumbnail();


//    public String getEtag() {
//	return etag;
//    }
//
//    public String getVideoId() {
//	return videoId;
//    }
//
//    public String getPublishedAt() {
//	return publishedAt;
//    }
//
//    public String getTitle() {
//	return title;
//    }
//
//    public String getDescription() {
//	return description;
//    }
//
//    public String getDefaultThumbnail() {
//	return defaultThumbnail;
//    }

    // public abstract BasicVideo setDefaultThumbnail(String thumb);
    // public abstract BasicVideo setEtag(String etag);
    // public abstract BasicVideo setVideoId(String videoId);
    // public abstract BasicVideo setPublishedAt(String publishedAt);
    // public abstract BasicVideo setTitle(String title);
    // public abstract BasicVideo setDescription(String description);
}
