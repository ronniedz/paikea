package cab.bean.srvcs.tube4kids.resources.utils;

import java.security.Key;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.keys.resolvers.VerificationKeyResolver;

public abstract class FederationConfig {
    public abstract  String getIssuer();
    public abstract  String getAudience();

    public Key getVerificationKey() {
	return null;
    }

    public AlgorithmConstraints getAlgorithmConstraints() {
	return null;
    }

    public float getTTL() {
	return 30;
    }

    public String getKeyId() {
	return null;
    }

    public String getSignatureAlgorithm() {
	return null;
    }

    public VerificationKeyResolver getVerificationKeyResolver() {
	return null;
    }
}
