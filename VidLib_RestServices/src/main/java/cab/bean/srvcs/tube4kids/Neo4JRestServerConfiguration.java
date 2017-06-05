package cab.bean.srvcs.tube4kids;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;
//import org.neo4j.driver.v1.AuthTokens;
//import org.neo4j.driver.v1.Driver;
//import org.neo4j.driver.v1.GraphDatabase;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Neo4JRestServerConfiguration {

	@NotEmpty
	@JsonProperty
	private String host = "localhost";

	@NotEmpty
	@JsonProperty
	private String protocol;

	@JsonProperty
	private String path;

	@Min(1)
	@Max(65535)
	@JsonProperty
	private Integer port;

	@NotEmpty
	@JsonProperty
	private String username = "neo4j";

	@JsonProperty
	private String password = "reggae";

	public String getHost() {
	    return host;
	}

	public String getProtocol() {
	    return protocol;
	}

	public String getPath() {
	    return path;
	}

	public Integer getPort() {
	    return port;
	}

	public String getUsername() {
	    return username;
	}

	public String getPassword() {
	    return password;
	}

	public URI getURI() {
	    URI uri = null;
	    try {
		//  new URI( scheme, userInfo,  host, port, path, query, fragment)
		uri = new URI(protocol, null, host, port, path, null, null);
	    } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return uri;
	}
}
