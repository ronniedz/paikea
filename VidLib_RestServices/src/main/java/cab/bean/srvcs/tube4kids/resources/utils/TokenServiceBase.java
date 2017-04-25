package cab.bean.srvcs.tube4kids.resources.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;

import cab.bean.srvcs.tube4kids.resources.utils.IdTokenVerity.UserValue;
import cab.bean.srvcs.tube4kids.resources.utils.SubjectData;

import com.google.common.base.Throwables;

public class TokenServiceBase {
    FederationConfig conf;
    
    public TokenServiceBase() { }
    public TokenServiceBase(FederationConfig conf) {
	this.conf = conf;
    }

    public JwtContext verify(String jwtString, FederationConfig conf) throws InvalidJwtException, JoseException, IOException {
	JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	.setRequireSubject()
	.setExpectedIssuers(true, conf.getIssuer())
	.setRequireExpirationTime()
	.setEvaluationTime(NumericDate.fromSeconds(1436388930))
	.setVerificationKey(conf.getVerificationKey())
	.setAllowedClockSkewInSeconds(30)
	.setExpectedAudience(conf.getAudience())
	.setJwsAlgorithmConstraints(conf.getAlgorithmConstraints())
	.setVerificationKeyResolver(conf.getSignatureVerificationKeyResolver()) // pretend to use Google's jwks endpoint to find the key for signature checks
	.build();

	    // Finally using the second JwtConsumer to actually validate the JWT. This operates on
	    // the JwtContext from the first processing pass, which avoids redundant parsing/processing.
	return  jwtConsumer.process(jwtString);
//	JwtClaims claims = jwtConsumer.processToClaims(jwtString);
    }
    
    public SubjectData verifyToData(String jwtString, FederationConfig conf) throws InvalidJwtException, JoseException, IOException {
	JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	.setRequireSubject()
	.setExpectedIssuers(true, conf.getIssuer())
	.setRequireExpirationTime()
	//.setEvaluationTime(NumericDate.fromSeconds(1436388930))
	.setVerificationKey(conf.getVerificationKey())
	.setAllowedClockSkewInSeconds(30)
	.setExpectedAudience(true, conf.getAudience())
	.setJwsAlgorithmConstraints(conf.getAlgorithmConstraints())
	.setVerificationKeyResolver(conf.getSignatureVerificationKeyResolver()) // pretend to use Google's jwks endpoint to find the key for signature checks
	.build();
	
//	SubjectData userValues = new SubjectData();
//	
//	jwtConsumer.processToClaims(jwtString).flattenClaims().entrySet().forEach(
//		entry -> {
//		    userValues.put(entry.getKey(),
//			    (entry.getValue().size() == 1) ? entry.getValue()
//				    .get(0) : entry.getValue());
//		}
//	);	    
   
	return  new SubjectData(  jwtConsumer.processToClaims(jwtString).getClaimsMap() );
    }
    
//    @Override
//    public JwtContext verify(JwtConsumer jwtConsumer, String jwtString) throws InvalidJwtException {
//	return  jwtConsumer.process(jwtString);
//    }
    
    public String generate(SubjectData data) {

		// Create the Claims, which will be the content of the JWT
	        final JwtClaims claims = new JwtClaims();
	        claims.setIssuer(conf.getIssuer()[0]);  // who creates the token and signs it
	        claims.setSubject(data.getSubject());
	        claims.setAudience(conf.getAudience()[0]); // to whom the token is intended to be sent
	        claims.setExpirationTimeMinutesInTheFuture(conf.getTokenExpiration()); // time when the token will expire
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
	        jws.setKeyIdHeaderValue(conf.getJwtIdPrefix());

	        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
//	        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
	        jws.setAlgorithmHeaderValue(conf.getAlgorithmIdentifier());

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
