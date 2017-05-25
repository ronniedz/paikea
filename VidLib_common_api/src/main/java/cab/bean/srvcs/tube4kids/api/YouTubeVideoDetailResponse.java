package cab.bean.srvcs.tube4kids.api;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import cab.bean.srvcs.tube4kids.core.VideoDetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a YouTube request via a managed proxy.
 *
 * <p>
 * The dialogue, via this object, is persisted, indexed and the results augmented if the YouTube search results contains new items.
 * A query is identified as unique based on URI and alphanumerically sorted name-value pairs in the query string.
 * Page-number and size params are excluded from the query string instead they are used in determining whether an update is in order.
 * </p>
 *
 * maxResults=5& "etag": "\"kiOs9cZLH2FUp6r6KJ8eyq_LIOk/d-2CjPwuHbdoXLB6u2gDFF8aPq0\"",
 "nextPageToken": "CAoQAA",
 "regionCode": "US",
 "pageInfo": {
  "totalResults": 1000000,
  "resultsPerPage": 10
 },

 *
 * @see cab.bean.srvcs.pipes.YoutTubeAPICallProcessor
 */

@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@NoArgsConstructor
@EqualsAndHashCode(exclude= {"error"})
@Data
public class YouTubeVideoDetailResponse {

    @JsonProperty
    private String etag;

    @JsonProperty
    private Long totalResults;

    @JsonProperty
    private List<VideoDetail> items;

    @JsonProperty(value="error")
    private ErrorBox error;

    /**
     * @param pageInfo
     *
     * In the form of:
     *
     *       pageInfo: {
     *       	totalResults: 356474,
     *       	resultsPerPage: 10
     *       }
     *
     */
    @JsonProperty(value="pageInfo", access=JsonProperty.Access.WRITE_ONLY)
    public void setPageInfo(Map<String, Object> pageInfo) {
	this.totalResults  = new Long((Integer) pageInfo.get("totalResults"));
    }

}


