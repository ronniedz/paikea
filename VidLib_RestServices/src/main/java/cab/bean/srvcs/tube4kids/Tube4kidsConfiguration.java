package cab.bean.srvcs.tube4kids;

import cab.bean.srvcs.tube4kids.core.Template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Driver;


public class Tube4kidsConfiguration extends Configuration {
    
    @Valid
    @NotNull
    @JsonProperty
    private NeoConfig neo4jConf = new NeoConfig();
    
    class NeoConfig {

	public NeoConfig() {
	    
	}
	@NotEmpty
	@JsonProperty
	private String url = "bolt://localhost";

	@NotEmpty
	@JsonProperty
	private String username = "neo4j";

	@NotEmpty
	@JsonProperty
	private String password = "reggae";
	
	public String getUrl() {
	    return url;
	}
	public String getUsername() {
	    return username;
	}
	public String getPassword() {
	    return password;
	}
    }
    
    private Driver neo4jDriver = GraphDatabase.driver( neo4jConf.getUrl(), AuthTokens.basic( neo4jConf.getUsername(), neo4jConf.getPassword() ) );;

    @NotEmpty
    private String template;
    
    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
    
    @Valid
    @NotNull
    private URL proxyUrl;
    
    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    @Valid
    private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory();
    
    @JsonProperty
    public URL getProxyUrl() {
	return proxyUrl;
    }
    
    @JsonProperty
    public void setProxyUrl(URL proxyUrl) {
	this.proxyUrl = proxyUrl;
    }

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public Template buildTemplate() {
        return new Template(template, defaultName);
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @JsonProperty("viewRendererConfiguration")
    public Map<String, Map<String, String>> getViewRendererConfiguration() {
        return viewRendererConfiguration;
    }

    @JsonProperty("viewRendererConfiguration")
    public void setViewRendererConfiguration(Map<String, Map<String, String>> viewRendererConfiguration) {
        final ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
        for (Map.Entry<String, Map<String, String>> entry : viewRendererConfiguration.entrySet()) {
            builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
        }
        this.viewRendererConfiguration = builder.build();
    }

    @JsonProperty("metrics")
    public GraphiteReporterFactory getGraphiteReporterFactory() {
        return graphiteReporterFactory;
    }

    @JsonProperty("metrics")
    public void setGraphiteReporterFactory(GraphiteReporterFactory graphiteReporterFactory) {
        this.graphiteReporterFactory = graphiteReporterFactory;
    }

    public byte[] getJwtTokenSecret() {
	// TODO Auto-generated method stub
	return "sdfghjhgfd".getBytes(Charset.forName("UTF-8"));
    }

    
    public Driver getNeo4jSession() {
	return this.neo4jDriver;
    }

}
