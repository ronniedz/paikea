package cab.bean.srvcs.pipes;

import javax.validation.constraints.NotNull;

import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

import cab.bean.srvcs.tube4kids.core.BasicVideo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@ToString
public class Configuration {

    @NotNull
    @JsonProperty
    private RestServerConfiguration restServerConfiguration = new RestServerConfiguration();
    
    @NotNull
    @JsonProperty
    private YoutubeResourceConfiguration youtubeResource = new YoutubeResourceConfiguration();
    
    
    public RestServerConfiguration getRestServerConfiguration() {
	return restServerConfiguration;
    }
    
    public YoutubeResourceConfiguration getYoutubeResourceConfiguration() {
	return youtubeResource;
    }

    public class YoutubeResourceConfiguration {
	
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

	/**
	 * @return the host
	 */
	public String getHost() {
	    return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
	    this.host = host;
	}

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
	    return contextPath;
	}

	/**
	 * @param contextPath the contextPath to set
	 */
	public void setContextPath(String contextPath) {
	    this.contextPath = contextPath;
	}

	/**
	 * @return the videoSearchPath
	 */
	public String getVideoSearchPath() {
	    return videoSearchPath;
	}

	/**
	 * @param videoSearchPath the videoSearchPath to set
	 */
	public void setVideoSearchPath(String videoSearchPath) {
	    this.videoSearchPath = videoSearchPath;
	}

	/**
	 * @return the videoDetailPath
	 */
	public String getVideoDetailPath() {
	    return videoDetailPath;
	}

	/**
	 * @param videoDetailPath the videoDetailPath to set
	 */
	public void setVideoDetailPath(String videoDetailPath) {
	    this.videoDetailPath = videoDetailPath;
	}
    
    }


    
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
    
    
    
        /**
         * @return the host
         */
        public String getHost() {
            return host;
        }
    
    
        /**
         * @param host the host to set
         */
        public void setHost(String host) {
            this.host = host;
        }
    
    
        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }
    
    
        /**
         * @param port the port to set
         */
        public void setPort(int port) {
            this.port = port;
        }
    
    
        /**
         * @return the contextPath
         */
        public String getContextPath() {
            return contextPath;
        }
    
    
        /**
         * @param contextPath the contextPath to set
         */
        public void setContextPath(String contextPath) {
            this.contextPath = contextPath;
        }
    
    
        /**
         * @return the searchServicePath
         */
        public String getSearchServicePath() {
            return searchServicePath;
        }
    
    
        /**
         * @param searchServicePath the searchServicePath to set
         */
        public void setSearchServicePath(String searchServicePath) {
            this.searchServicePath = searchServicePath;
        }
    
    
        /**
         * @return the detailServicePath
         */
        public String getDetailServicePath() {
            return detailServicePath;
        }
    
    
        /**
         * @param detailServicePath the detailServicePath to set
         */
        public void setDetailServicePath(String detailServicePath) {
            this.detailServicePath = detailServicePath;
        }
    }

}
