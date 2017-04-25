package cab.bean.srvcs.tube4kids.resources.utils;

import java.io.IOException;
import java.security.Key;
import java.util.List;

import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.VerificationJwkSelector;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.jose4j.lang.UnresolvableKeyException;

public class RefreshedResolver  implements VerificationKeyResolver
{
    private final VerificationJwkSelector selector = new VerificationJwkSelector();
    private final HttpsJwks httpsJWKS;
    
    public RefreshedResolver(HttpsJwks httpsJWKS)
    {
        this.httpsJWKS = httpsJWKS;
    }

    @Override
    public Key resolveKey(JsonWebSignature jws, List<JsonWebStructure> nestingContext) throws UnresolvableKeyException
    {
        JsonWebKey selected;
        List<JsonWebKey> jsonWebKeys;
	try {
	    jsonWebKeys = httpsJWKS.getJsonWebKeys();
	} catch (JoseException | IOException e1) {
            throw new UnresolvableKeyException(httpsJWKS.getLocation(), e1);
	}

        try
        {
            selected = selector.select(jws, jsonWebKeys);
        }
        catch (JoseException e)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to find a suitable verification key for JWS w/ header ").append(jws.getHeaders().getFullHeaderAsJsonString());
            sb.append(" due to an unexpected exception (").append(e).append(") selecting from keys: ").append(jsonWebKeys);
            throw new UnresolvableKeyException(sb.toString(), e);
        }

        if (selected == null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to find a suitable verification key for JWS w/ header ").append(jws.getHeaders().getFullHeaderAsJsonString());
            sb.append(" from JWKs ").append(jsonWebKeys);
            throw new UnresolvableKeyException(sb.toString());
        }

        return selected.getKey();
    }
}
