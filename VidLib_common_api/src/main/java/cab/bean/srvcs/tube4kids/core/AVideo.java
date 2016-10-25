/**
 * 
 */
package cab.bean.srvcs.tube4kids.core;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;

/**
 * @author ronalddennison
 *
 */
public interface AVideo {
 
    public String getEtag();
    public String getVideoId();
//    public String getPublishedAt();
    public String getTitle();
    public String getDescription();
    public String getDefaultThumbnail();
    
    
    // public abstract BasicVideo setDefaultThumbnail(String thumb);
    // public abstract BasicVideo setEtag(String etag);
    // public abstract BasicVideo setVideoId(String videoId);
    // public abstract BasicVideo setPublishedAt(String publishedAt);
    // public abstract BasicVideo setTitle(String title);
    // public abstract BasicVideo setDescription(String description);
}
