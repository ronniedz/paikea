package cab.bean.srvcs.tube4kids.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

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
