package cab.bean.srvcs.pipes;

import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("deprecation")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Data
@ToString
@NoArgsConstructor
public class Configuration {

    @NotNull
    @JsonProperty(value="db")
    public Map<String, String> db;

    
    @NotNull
    @JsonProperty
    public RestServerConfiguration restServerConfiguration = new RestServerConfiguration();
    
    @NotNull
    @JsonProperty
    public YoutubeResourceConfiguration youtubeResource = new YoutubeResourceConfiguration();

    public Map<String, String> getDb() {
	return db;
    }

    public RestServerConfiguration getRestServerConfiguration() {
	return restServerConfiguration;
    }
    
    public void setRestServerConfiguration(RestServerConfiguration rc) {
	this.restServerConfiguration = rc;
    }
    
    public YoutubeResourceConfiguration getYoutubeResourceConfiguration() {
	return youtubeResource;
    }

    public void setYoutubeResourceConfiguration(YoutubeResourceConfiguration ytc) {
	this.youtubeResource = ytc;
    }
    
    @EqualsAndHashCode
    @ToString
    @Data
    public class YoutubeResourceConfiguration  {
	@NotEmpty
	@JsonProperty
	private String apiKey;
	
	@NotEmpty
	@JsonProperty
	private String host;
	
	@NotEmpty
	@JsonProperty
	private String contextPath ;
	
	@NotEmpty
	@JsonProperty
	private String videoSearchPath;
	
	@NotEmpty
	@JsonProperty
	private String videoDetailPath;
    }
    
    @EqualsAndHashCode
    @ToString
    @Data
    public class RestServerConfiguration {
        @NotEmpty
        @JsonProperty
        private String host;

        @NotEmpty
        @JsonProperty
        private int port;

        @NotEmpty
        @JsonProperty
        private String contextPath ;

        @NotEmpty
        @JsonProperty
        private String searchServicePath;

        @NotEmpty
        @JsonProperty
        private String detailServicePath;
    }

}
