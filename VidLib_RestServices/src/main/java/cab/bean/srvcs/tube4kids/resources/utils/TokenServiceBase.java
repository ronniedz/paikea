package cab.bean.srvcs.tube4kids.resources.utils;

import java.util.List;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;

import com.google.common.base.Throwables;

public class TokenServiceBase extends aTokenService {

    
    public TokenServiceBase() { }
    
    @Override
    public JwtClaims parse(String jwtString) throws InvalidJwtException {
        //The first JwtConsumer is basically just used to parse the JWT into a JwtContext object.
	//return jwtContext.getJwtClaims().flattenClaims();
	return jwtRrelaxedConsumer.processToClaims(jwtString);
	
    }

    @Override
    public JwtContext verify(String jwtString, FederationConfig conf) throws InvalidJwtException {
	JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	.setRequireSubject()
	.setExpectedIssuer(conf.getIssuer())
	.setRequireExpirationTime()
	.setEvaluationTime(NumericDate.fromSeconds(1436388930))
	.setVerificationKey(conf.getVerificationKey())
	.setAllowedClockSkewInSeconds(30)
	.setExpectedAudience(conf.getAudience())
	.setJwsAlgorithmConstraints(conf.getAlgorithmConstraints())
	.setVerificationKeyResolver(conf.getVerificationKeyResolver()) // pretend to use Google's jwks endpoint to find the key for signature checks
	.build();

	    // Finally using the second JwtConsumer to actually validate the JWT. This operates on
	    // the JwtContext from the first processing pass, which avoids redundant parsing/processing.
	return  jwtConsumer.process(jwtString);
//	JwtClaims claims = jwtConsumer.processToClaims(jwtString);
    }
    
//    @Override
//    public JwtContext verify(JwtConsumer jwtConsumer, String jwtString) throws InvalidJwtException {
//	// Finally using the second JwtConsumer to actually validate the JWT. This operates on
//	// the JwtContext from the first processing pass, which avoids redundant parsing/processing.
//	return  jwtConsumer.process(jwtString);
//    }
    
    @Override
    public String generate(SubjectData data, FederationConfig conf) {

		// Create the Claims, which will be the content of the JWT
	        final JwtClaims claims = new JwtClaims();
	        claims.setIssuer(conf.getIssuer());  // who creates the token and signs it
	        claims.setSubject(data.getSubject());
	        claims.setAudience(conf.getAudience()); // to whom the token is intended to be sent
	        claims.setExpirationTimeMinutesInTheFuture(conf.getTTL()); // time when the token will expire
//	        claims.setGeneratedJwtId(); // a unique identifier for the token
	        claims.setIssuedAtToNow();  // when the token was issued/created (now)
	        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)

	        data.getClaims().entrySet().forEach(x -> { 
	            if ( x.getValue() instanceof java.util.Collection) {
	                claims.setStringListClaim(x.getKey(), (List<String>) x.getValue()); // multi-valued claims work too and will end up as a JSON array
	            } else {
	        		claims.setClaim(x.getKey(), x.getValue()); // additional claims/attributes about the subject can be added
	            }
	        });

	        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
	        // In this example it is a JWS so we create a JsonWebSignature object.
	        JsonWebSignature jws = new JsonWebSignature();

	        // The payload of the JWS is JSON content of the JWT Claims
	        jws.setPayload(claims.toJson());

	        // The JWT is signed using the private key
//	        jws.setKey(rsaJsonWebKey.getPrivateKey());
	        jws.setKey(conf.getVerificationKey());

	        // Set the Key ID (kid) header because it's just the polite thing to do.
	        // We only have one key in this example but a using a Key ID helps
	        // facilitate a smooth key rollover process
//	        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
	        jws.setKeyIdHeaderValue(conf.getKeyId());

	        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
//	        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
	        jws.setAlgorithmHeaderValue(conf.getSignatureAlgorithm());

	        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
	        // representation, which is a string consisting of three dot ('.') separated
	        // base64url-encoded parts in the form Header.Payload.Signature
	        // If you wanted to encrypt it, you can simply set this jwt as the payload
	        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
	        try {
	            return  jws.getCompactSerialization();
	        }
	        catch (JoseException e) { throw Throwables.propagate(e); }
    }

//    @Override
//    public Map<String, Object> refresh(String jwtString, FederationConfig conf) {
//	// TODO Auto-generated method stub
//	return null;
//    }
//
    
}
