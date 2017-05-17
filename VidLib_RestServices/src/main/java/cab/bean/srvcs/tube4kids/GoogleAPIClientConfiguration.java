package cab.bean.srvcs.tube4kids;

import javax.validation.constraints.NotNull;

import cab.bean.srvcs.tube4kids.auth.FederationConfig;

import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;

public class GoogleAPIClientConfiguration extends FederationConfig {

    @NotNull
    protected String signatureKeyURL = GoogleOAuthConstants.DEFAULT_PUBLIC_CERTS_ENCODED_URL;

}
