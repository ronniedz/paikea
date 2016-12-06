package cab.bean.srvcs.tube4kids.core;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import lombok.NonNull;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Setter
@Entity
@Table(name = "video_detail")
@NamedQueries({ @NamedQuery(
	name = "cab.bean.srvcs.tube4kids.core.VideoDetail.findAll",
	query = "SELECT vid FROM VideoDetail vid")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class VideoDetail {

    private String etag;
    private String id;

    
    private String defaultLanguage; //   snippet.defaultLanguage
    private String defaultAudioLanguage; //   snippet.defaultAudioLanguage
    private List<String> tags; //   snippet.tags
    
    
    private Boolean caption; //   contentDetails.caption
    private String duration; //   contentDetails.duration
    private Boolean licensedContent; //   contentDetails.licensedContent: false
    private Video video;

    
    @Embedded
    public RegionRestriction regionRestriction = new RegionRestriction(); //   contentDetails.regionRestriction
 

    @Id     
    @NonNull
    @Column(name = "id", unique = true, nullable = false)
    @JsonProperty
    public String getId() {
        return id;
    }

    
    @MapsId
    @OneToOne
    @JoinColumn(name="id")
    public Video getVideo() {
        return video;
    }


    @JsonProperty 
    public String getEtag() {
        return etag;
    }

    @JsonProperty
    public void setId(String ytid) {
	this.id  = ytid;
    }
    

    @SuppressWarnings("unchecked")
    @JsonSetter(value="snippet")
    public void setSnippet(Map<String, Object> snippet) {
	//   snippet.defaultLanguage
	this.defaultLanguage  = (String) snippet.get("defaultLanguage");

	//   snippet.defaultAudioLanguage
	this.defaultAudioLanguage  = (String) snippet.get("defaultAudioLanguage");
	
	//   snippet.tags	
	this.tags  = (List<String>) snippet.get("tags");
    }
 
    
    @JsonProperty(value="contentDetails", access=JsonProperty.Access.WRITE_ONLY)
    public void setContentDetails(Map<String, Object> contentDetails) {
	//   contentDetails.duration
	this.duration  = (String) contentDetails.get("duration");
	
	//   contentDetails.licensedContent
	this.licensedContent  = contentDetails.containsKey("licensedContent") ? Boolean.valueOf((String)contentDetails.get("licensedContent")) : false;
	
	//   contentDetails.caption
	this.caption  = contentDetails.containsKey("caption") ? Boolean.valueOf((String)contentDetails.get("caption")) : false;
	
	//   contentDetails.licensedContent
	this.licensedContent  = (Boolean) contentDetails.get("licensedContent");
    }
    
    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setVideoId(String videoId) {
        this.id = videoId;
    }

    public Boolean getCaption() {
        return caption;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultAudioLanguage() {
        return defaultAudioLanguage;
    }

    @JsonProperty
    public String getYoutubeTags() {
	final StringJoiner joiner = new StringJoiner(",");
	tags.forEach(x -> { joiner.add(x); });
	return joiner.toString();
    }

    @JsonProperty
    public String getDuration() {
        return duration;
    }

    @JsonProperty
    public Boolean isLicensedContent() {
        return licensedContent;
    }

    @JsonProperty
    public RegionRestriction getRegionRestriction() {
        return regionRestriction;
    }

}

