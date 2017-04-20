package cab.bean.srvcs.tube4kids.resources.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cab.bean.srvcs.tube4kids.GoogleAPIClientConfiguration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.common.base.Throwables;

public class IdTokenVerity {
    
    private final HttpTransport transport = new NetHttpTransport();
    private String clientId;
    private JsonFactory jsonFactory;
    
    public IdTokenVerity(String clientId, JsonFactory jsonFactory) {
	this.jsonFactory = jsonFactory;
	this.clientId = clientId;
    }

    private GoogleIdTokenVerifier getVerifier() {
	return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
	.setAudience(Collections.singletonList(clientId)).build();
    }

    
    /**
     * Parses the given ID token string and returns an  {@link GoogleIdToken}.
     *
     * @param jsonFactory JSON factory
     * @param idTokenString ID token string
     * @return parsed Google ID token
     */
    public GoogleIdToken parse(String idTokenString) throws IOException {
	JsonWebSignature jws = JsonWebSignature.parser(jsonFactory).setPayloadClass(Payload.class).parse(idTokenString);

	return
		new GoogleIdToken(
			jws.getHeader(), 
			(Payload) jws.getPayload(),
			jws.getSignatureBytes(),
			jws.getSignedContentBytes());
    }

    public UserValue verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
	UserValue m = null;

//	GoogleIdToken idToken = GoogleIdToken.parse( jsonFactory, idTokenString );
	final GoogleIdToken idToken = getVerifier().verify(idTokenString);
	
        if (idToken != null) {
            final Payload payload = idToken.getPayload();

            m = new UserValue();
            m.put("issuer", payload.getIssuer());
            m.put("subject", payload.getSubject());
            m.put("email", payload.getEmail());
            m.put("email_verified", payload.getEmailVerified());
            m.put("firstname", payload.getUnknownKeys().get("given_name"));
            m.put("lastname", payload.getUnknownKeys().get("family_name"));
            m.put("picture", payload.getUnknownKeys().get("picture"));
            m.put("locale", payload.getUnknownKeys().get("locale"));
        }
        return m;
    }

    public class UserValue extends HashMap<String, Object> {

	public String getSubject() {
	    return (String) get("subject");
	}

	public String getIssuer() {
	    return (String) get("issuer");
	}

	public String removeIssuer() {
	    return (String) remove("issuer");
	}
    }


}
