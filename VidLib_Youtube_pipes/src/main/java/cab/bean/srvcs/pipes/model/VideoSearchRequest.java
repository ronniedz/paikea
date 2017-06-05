package cab.bean.srvcs.pipes.model;

import javax.persistence.Id;
import javax.ws.rs.QueryParam;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("deprecation")
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class VideoSearchRequest {

    @Id
    // ObjectId
    private String id;

    @JsonProperty
    private String collectionName;

    @JsonProperty
    private String etag;

    @JsonProperty
    private String lastId;

    @JsonProperty
    @QueryParam("maxResults")
    private int maxResults;

    @JsonProperty
    private String nextPageToken;

    @QueryParam("pageToken")
    private String pageToken;

    @JsonProperty
    private String prevPageToken;

    @JsonProperty
    @QueryParam("regionCode")
    private String regionCode;

    @QueryParam("q")
    private String q;

    @JsonProperty
    private Long totalResults;

}
