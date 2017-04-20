package cab.bean.srvcs.tube4kids.resources.utils;

import java.security.Key;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.keys.resolvers.VerificationKeyResolver;

public interface FederationConfig {
    public String getIssuer();
    public Key getVerificationKey();
    public AlgorithmConstraints getAlgorithmConstraints();
    public String getAudience();
    public float getTTL();
    public String getKeyId();
    public String getSignatureAlgorithm();
    public VerificationKeyResolver getVerificationKeyResolver();
}
