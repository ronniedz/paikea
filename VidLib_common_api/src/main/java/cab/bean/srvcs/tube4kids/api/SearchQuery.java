package cab.bean.srvcs.tube4kids.api;

import javax.ws.rs.QueryParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Accessors(fluent = true)
@Data
public class SearchQuery {

    @JsonProperty
    @QueryParam("pageToken")
    private String pageToken;
    
    @JsonProperty
    @QueryParam("maxResults")
    private int maxResults;
    
    @JsonProperty
    @QueryParam("etag")
    private String etag;

    @JsonProperty
    @QueryParam("q")
    private String q;

    @JsonIgnore
   public void setMaxResults(String string) throws NumberFormatException {
	this.maxResults = Integer.parseInt(string);
    }

}
