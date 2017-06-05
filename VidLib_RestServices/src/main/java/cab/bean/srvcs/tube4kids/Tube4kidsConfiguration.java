package cab.bean.srvcs.tube4kids;

import io.dropwizard.Configuration;
import io.dropwizard.bundles.assets.AssetsBundleConfiguration;
import io.dropwizard.bundles.assets.AssetsConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import cab.bean.srvcs.tube4kids.config.RestServerConfiguration;
import cab.bean.srvcs.tube4kids.config.YoutubeResourceConfiguration;
import cab.bean.srvcs.tube4kids.core.Template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;


public class Tube4kidsConfiguration extends Configuration  implements AssetsBundleConfiguration {

    @Valid
    @NotNull
    private String appContextUri;

    @NotNull
    @JsonProperty
    private Neo4JRestServerConfiguration neo4jConf = new Neo4JRestServerConfiguration();

    @NotEmpty
    private String template;


    @NotEmpty
    private String testUrl;

    @NotEmpty
    private String defaultName = "Guest";

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private URL proxySearchUrl;

    @Valid
    @NotNull
    private URL proxyDetailUrl;

    @NotNull
    private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

    @JsonProperty
    private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory();

    @NotNull
    @JsonProperty
    private AssetsConfiguration assets;
//     private final AssetsConfiguration assets = AssetsConfiguration.builder().build();

    @NotNull
    @JsonProperty
    public final GoogleAPIClientConfiguration googleAPIClientConfiguration = new GoogleAPIClientConfiguration();

    @NotNull
    @JsonProperty
    public final JWTConfiguration jwtConfiguration = new JWTConfiguration();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
      return assets;
    }

    @NotNull
    public RestServerConfiguration restServerConfiguration;

    @NotNull
    public YoutubeResourceConfiguration youtubeResource;

    @JsonProperty
    public RestServerConfiguration getRestServerConfiguration() {
	return restServerConfiguration;
    }

    public void setRestServerConfiguration(RestServerConfiguration conf) throws MalformedURLException {
	this.restServerConfiguration = conf;
	final URL baseUrl =new URL("http" , conf.getHost(), conf.getPort(), conf.getContextPath());
	proxySearchUrl = new URL(baseUrl, conf.getSearchServicePath());
	proxyDetailUrl = new URL(baseUrl, conf.getDetailServicePath());
    }

    @JsonProperty
    public YoutubeResourceConfiguration getYoutubeResourceConfiguration() {
	return youtubeResource;
    }

    public void setYoutubeResourceConfiguration(YoutubeResourceConfiguration conf) throws MalformedURLException {
	this.youtubeResource = conf;
	new URL("http" , conf.getHost(), conf.getContextPath());
    }

    public URL getProxySearchUrl() {
	return proxySearchUrl;
    }

    @JsonProperty
    public URL getProxyDetailUrl() {
        return proxyDetailUrl;
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

    public Driver getNeo4jDriver() {
	return  GraphDatabase.driver(
	    neo4jConf.getURI(),
	    AuthTokens.basic( neo4jConf.getUsername(), neo4jConf.getPassword())
	);
    }

    @JsonProperty
    public GoogleAPIClientConfiguration getGoogleAPIClientConfiguration() {
        return googleAPIClientConfiguration;
    }

    @JsonProperty
    public JWTConfiguration getJwtConfiguration() {
        return jwtConfiguration;
    }

    public String getAppContextUri() {
	return appContextUri ;
    }

    /**
     * @param appContextUri the appContextUri to set
     */
    @JsonProperty
    public void setAppContextUri(String appUriPrefix) {
        this.appContextUri = appUriPrefix;
    }

    /**
     * @return the testUrl
     */
    @JsonProperty
    public String getTestUrl() {
        return testUrl;
    }

    /**
     * @param testUrl the testUrl to set
     */
    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }


}
