package cab.bean.srvcs.pipes;

import java.util.HashMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper=false)
@ToString
@Data
@NoArgsConstructor
public class YoutubeResourceConfiguration extends HashMap<String, String> {

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

	public String getApiKey() {
	    return apiKey;
	}

	public void setApiKey(String apiKey) {
	    this.apiKey = apiKey;
	    put("apiKey", apiKey);
	}

	public String getHost() {
	    return host;
	}

	public void setHost(String host) {
	    this.host = host;
	    put("host", host);
	}

	public String getContextPath() {
	    return contextPath;
	}

	public void setContextPath(String contextPath) {
	    this.contextPath = contextPath;
	    put("contextPath", contextPath);
	}

	public String getVideoSearchPath() {
	    return videoSearchPath;
	}

	public void setVideoSearchPath(String videoSearchPath) {
	    this.videoSearchPath = videoSearchPath;
	    put("videoSearchPath", videoSearchPath);
	}

	public String getVideoDetailPath() {
	    return videoDetailPath;
	}

	public void setVideoDetailPath(String videoDetailPath) {
	    this.videoDetailPath = videoDetailPath;
	    put("videoDetailPath", videoDetailPath);
	}
}

