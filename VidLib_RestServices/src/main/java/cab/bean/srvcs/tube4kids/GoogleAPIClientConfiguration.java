package cab.bean.srvcs.tube4kids;

import cab.bean.srvcs.tube4kids.core.Template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
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
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Driver;


public class GoogleAPIClientConfiguration  {
    
    private String appClientId;
    private String certsUrlString = GoogleOAuthConstants.DEFAULT_PUBLIC_CERTS_ENCODED_URL;
    private java.util.Collection<String>  issuerAliases;


    /**
     * @return the appClientId
     */
    public String getAppClientId() {
        return appClientId;
    }
    /**
     * @param appClientId the appClientId to set
     */
    public void setAppClientId(String appClientId) {
        this.appClientId = appClientId;
    }
    /**
     * @return the certsUrlString
     */
    public String getCertsUrlString() {
        return certsUrlString;
    }
    /**
     * @param certsUrlString the certsUrlString to set
     */
    public void setCertsUrlString(String certsUrlString) {
        this.certsUrlString = certsUrlString;
    }
    /**
     * @return the issuerAliases
     */
    public java.util.Collection<String> getIssuerAliases() {
        return issuerAliases;
    }
    /**
     * @param issuerAliases the issuerAliases to set
     */
    public void setIssuerAliases(java.util.Collection<String> issuerAliases) {
        this.issuerAliases = issuerAliases;
    }

    
}
