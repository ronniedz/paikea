package cab.bean.srvcs.tube4kids.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@RequiredArgsConstructor
@ToString( exclude={"video" })
@EqualsAndHashCode( exclude={"video" })
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
public class VideoDetail {

    private String etag;
    private String id;
    private String videoId;
    private String defaultLanguage; //   snippet.defaultLanguage
    private String defaultAudioLanguage; //   snippet.defaultAudioLanguage
    private List<String> tags; //   snippet.tags
    private Boolean caption; //   contentDetails.caption
    private String duration; //   contentDetails.duration
    private Boolean licensedContent; //   contentDetails.licensedContent: false
    private Video video;

    @Id     
    @NonNull
    @Column(name="video_id")
    @JsonIgnore
    public String getVideoId() {
        return videoId;
    }
    
    @JsonIgnore
    public String getId() {
	return id;
    }

    @Embedded
    public RegionRestriction regionRestriction = new RegionRestriction(); //   contentDetails.regionRestriction
 

    @OneToOne(optional = false, fetch=FetchType.LAZY)
    @MapsId
    @JsonIgnore
    public Video getVideo() {
        return video;
    }
    
    @JsonProperty 
    public String getEtag() {
        return etag;
    }

    @JsonSetter
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
	//   contentDetails.duration : string
	this.duration  = (String) contentDetails.get("duration");
	
	//   contentDetails.licensedContent : boolean
	this.licensedContent  =  Optional.of((Boolean) contentDetails.get("licensedContent")).orElse(Boolean.FALSE);
	
	//   contentDetails.caption : string
	this.caption  = contentDetails.containsKey("caption") ? Boolean.valueOf((String)contentDetails.get("caption")) : Boolean.FALSE;

    }
    
    public void setEtag(String etag) {
        this.etag = etag;
    }

//    public void setVideoId(String videoId) {
//        this.videoId = videoId;
//    }

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    public Boolean getCaption() {
        return caption;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getDefaultAudioLanguage() {
        return defaultAudioLanguage;
    }

    @Lob
    @Column(name="youtube_tags")
    public String getYoutubeTags() {
	final StringJoiner joiner = new StringJoiner(",");
	tags.forEach(x -> { joiner.add(x); });
	return joiner.toString();
    }

    public void setYoutubeTags(String utags) {
	tags = Arrays.asList(utags.split("\\s*,\\s*"));
    }

    @JsonProperty
    public String getDuration() {
        return duration;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @JsonProperty
    public Boolean isLicensedContent() {
        return licensedContent;
    }

    @JsonProperty
    public RegionRestriction getRegionRestriction() {
        return regionRestriction;
    }

    @Transient @JsonProperty
    public List<String> getTags() {
        return tags;
    }

}

