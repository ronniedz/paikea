package cab.bean.srvcs.tube4kids;

import javax.validation.constraints.NotNull;

import cab.bean.srvcs.tube4kids.auth.FederationConfig;

/** Constants from: com.google.api-client::google-api-client::1.22.0 */
public class GoogleAPIClientConfiguration extends FederationConfig {

    /** Encoded URL of Google's end-user authorization server. */
    public static final String AUTHORIZATION_SERVER_URL = "https://accounts.google.com/o/oauth2/auth";

    /** Encoded URL of Google's token server. */
    public static final String TOKEN_SERVER_URL = "https://accounts.google.com/o/oauth2/token";

    public static final String DEFAULT_PUBLIC_CERTS_ENCODED_URL =
        "https://www.googleapis.com/oauth2/v1/certs";

    /**
     * Redirect URI to use for an installed application as specified in <a
     * href="http://code.google.com/apis/accounts/docs/OAuth2InstalledApp.html">Using OAuth 2.0 for
     * Installed Applications</a>.
     */
    public static final String OOB_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    @NotNull
    protected String signatureKeyURL = DEFAULT_PUBLIC_CERTS_ENCODED_URL;

}
