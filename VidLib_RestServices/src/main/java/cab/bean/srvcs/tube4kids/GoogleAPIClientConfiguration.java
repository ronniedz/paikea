package cab.bean.srvcs.tube4kids;

import javax.validation.constraints.NotNull;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;


public class GoogleAPIClientConfiguration  {
    
    @NotNull
    private String appClientId;

    @NotNull
    private String certificatesURL = GoogleOAuthConstants.DEFAULT_PUBLIC_CERTS_ENCODED_URL;

    @NotNull
    private java.util.List<String>  issuerAliases;


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
     * @return the certificatesURL
     */
    public String getCertificatesURL() {
        return certificatesURL;
    }
    /**
     * @param certificatesURL the certificatesURL to set
     */
    public void setCertificatesURL(String certsUrlString) {
        this.certificatesURL = certsUrlString;
    }
    /**
     * @return the issuerAliases
     */
    public java.util.List<String> getIssuerAliases() {
        return issuerAliases;
    }
    /**
     * @param issuerAliases the issuerAliases to set
     */
    public void setIssuerAliases(java.util.List<String> issuerAliases) {
        this.issuerAliases = issuerAliases;
    }

    
}
