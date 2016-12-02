package cab.bean.srvcs.tube4kids.api;

import java.util.List;
import java.util.Map;

import javax.ws.rs.QueryParam;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import cab.bean.srvcs.tube4kids.core.Video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class Qwrap {


    @JsonProperty
    private List<String> actions;
    
    @JsonProperty
    private String etag;

    @JsonProperty
    private String nextPageToken;

    @JsonProperty
    private String prevPageToken;
    
    @JsonProperty
    private String regionCode;
    
    @JsonProperty
    private Long totalResults;
    
    @JsonProperty
    private List<Video> items;

    @JsonProperty(value="error")
    private ErrorBox error;
  
    @JsonProperty(value="error")
    private Map status;


/*
...
    pageInfo: {
	totalResults: 356474,
	resultsPerPage: 10
    }
*/

    @JsonProperty(value="pageInfo", access=JsonProperty.Access.WRITE_ONLY)
    public void setPageInfo(Map<String, Object> pageInfo) {
	this.totalResults  = new Long((Integer) pageInfo.get("totalResults"));
    }

}        


