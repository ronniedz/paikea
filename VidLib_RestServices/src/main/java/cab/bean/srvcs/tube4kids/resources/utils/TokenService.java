package cab.bean.srvcs.tube4kids.resources.utils;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtContext;

public class TokenService extends TokenServiceBase {
    private final FederationConfig conf;

    public TokenService(FederationConfig conf) {
	this.conf = conf;
    }
    
    public JwtContext verify(String tokenString) throws InvalidJwtException {
	return  verify(tokenString, conf);
    }

    public SubjectData verifyToData(String tokenString) throws InvalidJwtException {
	return  verifyToData(tokenString, conf);
    }
    
    public String generate(SubjectData data) {
	return  generate(data, conf);
    }
}
