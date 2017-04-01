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
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Driver;


public class GoogleAPIClientConfiguration extends HashMap<String, String> {
    
//    private String jwtTokenSecret;
    
//    private String clientId;
  

//    public void setJwtTokenSecret(String jwtTokenSecret) {
//	this.put("jwtTokenSecret", jwtTokenSecret);
//    }
//
//    public void setClientId(String clientId) {
//	this.put("clientId", clientId);
//    }
//
    public String getClientId() {
	return this.get("clientId");
    }
//
//    public String getJwtTokenSecret() {
////        return jwtTokenSecret.getBytes("UTF-8");
//        return this.get("jwtTokenSecret");
//    }

    public String getAudience() {
	return this.get("clientId");
   }

}
