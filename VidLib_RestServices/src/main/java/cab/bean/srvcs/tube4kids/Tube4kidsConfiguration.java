package cab.bean.srvcs.tube4kids;

import cab.bean.srvcs.tube4kids.core.Template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import io.dropwizard.Configuration;
import io.dropwizard.bundles.assets.AssetsBundleConfiguration;
import io.dropwizard.bundles.assets.AssetsConfiguration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Driver;


public class Tube4kidsConfiguration extends Configuration  implements AssetsBundleConfiguration {
    
    @Valid
    @NotNull
    @JsonProperty
    private Neo4JRestServerConfiguration neo4jConf = new Neo4JRestServerConfiguration();
    
    @NotEmpty
    private String template;
    
    @NotEmpty
    private String defaultName = "Stranger";

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

    @Valid
    private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory();
    
    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = AssetsConfiguration.builder().build();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
      return assets;
    }

    @JsonProperty
    public URL getProxySearchUrl() {
	return proxySearchUrl;
    }
    
    @JsonProperty
    public void setProxySearchUrl(URL proxyUrl) {
	this.proxySearchUrl = proxyUrl;
    }

    @JsonProperty
    public URL getProxyDetailUrl() {
        return proxyDetailUrl;
    }

    @JsonProperty
    public void setProxyDetailUrl(URL proxyDetailUrl) {
        this.proxyDetailUrl = proxyDetailUrl;
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

    @NotEmpty
//    private String jwtTokenSecret = "dfwzsdzwh823zebdwdz772632gdsbd";
    private String jwtTokenSecret = "MIIDJjCCAg6gAwIBAgIIRvo0sON9ghswDQYJKoZIhvcNAQEFBQAwNjE0MDIGA1UE AxMrZmVkZXJhdG"
    	+ "VkLXNpZ25vbi5zeXN0ZW0uZ3NlcnZpY2VhY2NvdW50LmNvbTAe Fw0xNzAzMTgxMTQzMzRaFw0xNzA"
    + "zMjExMjEzMzRaMDYxNDAyBgNVBAMTK2ZlZGVy YXRlZC1zaWdub24uc3lzdGVtLmdzZXJ2aWNlYWNjb3"
    	+ "VudC5jb20wggEiMA0GCSqG SIb3DQEBAQUAA4IBDwAwggEKAoIBAQDKusBxoJVow5QkfWZ4eahqe//HB"
    + "PiZiTHd LA6AQYtZl23WEPw9OsF8mfrEroG1iWEynqCmdBfabH+0j9vLaATmYwJyqVZS8ebP y5/FkHsaMqV"
    	+ "xmxtWik4djJTsMXp8TYS7muwCvjrgnAHaaurYnvQ0XzZa7+b52ZTG wIQMXPxDyCL8iI206c5Qmm8hfOyNM"
    + "JPqvyJWg12d/utyPiHAEC8Xe+fiJ8I5d65l ipNgmKiNX56L1U7zZSvyrlLa3KFSCYnQbji9MU7JyTwAiZiRaYeUA"
    	+ "+tk58z6x68A CisDYNiGrJsKaXDsFo+BCYeol/qz4RyzZChbAff8EYzzcDSxlSNjAgMBAAGjODA2 MAwGA1UdE"
    + "wEB/wQCMAAwDgYDVR0PAQH/BAQDAgeAMBYGA1UdJQEB/wQMMAoGCCsG AQUFBwMCMA0GCSqGSIb3DQEBBQUAA4IBAQAFjXMzgmrx+y/WR1jaLNvmqscE8Ga8 T5xX+Qwtfqdh7X0hEc3ROglS"
    	+ "O7Hah9EsnjjBfXD6KD776fnbc06ieQhI9HkxCRw+ wnWuwwBwjcFrqeovRcBbKFtNMPNWo2cX4p/t4ebpJ3tctYNjk9qXBsJlPrZkz4yB SgV963r4CaJ8rPJaiItMIh6nRplZhZsCJkLKjqt+jGgeXBb/FVoE/IS34rwVmKui 0pR2/9+bILZEtldH1cO2ffzD4FzqUirmN3UfXN8JG02jHmxfQ+/CMZ05oXyjYwcP asfBBbDz7"
    + "7ymDveEkvsSPgrzCkXBzrEpMUtttlXnt0nVzH2VGE/av7ED";
    

    public String getJwtTokenSecret() {
//        return jwtTokenSecret.getBytes("UTF-8");
        return jwtTokenSecret;
    }

    public Driver getNeo4jDriver() {
	return  GraphDatabase.driver(
	    neo4jConf.getURI(),
	    AuthTokens.basic( neo4jConf.getUsername(), neo4jConf.getPassword())
	);
    }

    public String getClientId() {
	return "16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com";
    }

}
