package cab.bean.srvcs.tube4kids.config;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

@EqualsAndHashCode(exclude = { "proxyDetailUrl", "proxySearchUrl", "proxyDomainUrl" } )
@ToString(exclude = { "proxyDetailUrl", "proxySearchUrl", "proxyDomainUrl" } )
@Data
public class RestServerConfiguration {
    @NotEmpty
    @JsonProperty
    private String host;

    @NotEmpty
    @JsonProperty
    private int port;

    @NotEmpty
    @JsonProperty
    private String contextPath ;

    @NotEmpty
    @JsonProperty
    private String searchServicePath;

    @NotEmpty
    @JsonProperty
    private String detailServicePath;

    private URL proxyDetailUrl;
    private URL proxySearchUrl;
    private URL proxyDomainUrl;

    public URL getProxyDomainURL() throws MalformedURLException {
	return ( proxyDomainUrl == null ) ? proxyDomainUrl = new URL("http" , getHost(), getPort(), contextPath) : proxyDomainUrl;
    }

    public URL getProxySearchURL() throws MalformedURLException {
	return ( proxySearchUrl == null ) ? proxySearchUrl = new URL(getProxyDomainURL(), searchServicePath) : proxySearchUrl;
    }

    public URL getProxyDetailURL() throws MalformedURLException {
	return ( proxyDetailUrl == null ) ? proxyDetailUrl = new URL(getProxyDomainURL(), detailServicePath) : proxyDetailUrl;
    }

}

