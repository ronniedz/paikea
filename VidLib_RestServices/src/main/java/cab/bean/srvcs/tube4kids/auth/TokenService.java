package cab.bean.srvcs.tube4kids.auth;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;

import cab.bean.srvcs.tube4kids.auth.utils.SubjectData;
import cab.bean.srvcs.tube4kids.exception.ConfigurationException;

import com.google.common.base.Throwables;

public class TokenService {

    public final static JwtConsumer jwtRrelaxedConsumer = new JwtConsumerBuilder()
    .setSkipAllValidators()
    .setDisableRequireSignature()
    .setSkipSignatureVerification().build();

    private final FederationConfig conf;

    public TokenService() {
	this.conf = null;
    }

    public TokenService(final FederationConfig conf) {
	this.conf = conf;
    }

    /**
     * Parses the ID token string and returns a {@link JwtClaims} object.
     *
     * @param idTokenString ID token string
     * @return parsed JwtClaims
     */
    public static JwtClaims parse(String tokenString) throws InvalidJwtException{
    	return jwtRrelaxedConsumer.processToClaims(tokenString);
    }

    /**
     * Parses the ID token string and returns a Map of the data as a {@link SubjectData}.
     *
     * @param idTokenString ID token string
     * @return a SubjectData
     */
    public static SubjectData parseToData(String tokenString) throws InvalidJwtException{
	return new SubjectData(jwtRrelaxedConsumer.processToClaims(tokenString).getClaimsMap());
    }

    public JwtContext verify(String jwtString) throws InvalidJwtException, JoseException, IOException, ConfigurationException {
//	.setRequireExpirationTime()
//	.setEvaluationTime(NumericDate.fromSeconds(1436388930))
	return conf.getConsumer().build().process(jwtString);
    }

    public SubjectData verifyToData(String jwtString) throws InvalidJwtException, JoseException, IOException, ConfigurationException {
	return new SubjectData(
		conf.getConsumer()
		.build().processToClaims(jwtString).getClaimsMap()
	);
    }

    public String generate(SubjectData data, String apiIdentifier) {

		// Create the Claims, which will be the content of the JWT
	        final JwtClaims claims = new JwtClaims();
	        claims.setIssuer(apiIdentifier != null ? apiIdentifier : conf.getIssuer()[0]);  // who creates the token and signs it
	        claims.setSubject(data.getSubject());
	        claims.setAudience(conf.getAudience()); // to whom the token is intended to be sent
	        claims.setExpirationTimeMinutesInTheFuture(conf.getTokenExpiration()); // time when the token will expire
	        claims.setJwtId(UUID.randomUUID().toString());
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

    public String generate(SubjectData data) {

	// Create the Claims, which will be the content of the JWT
	final JwtClaims claims = new JwtClaims();
	claims.setIssuer(conf.getIssuer()[0]);  // who creates the token and signs it
	claims.setSubject(data.getSubject());
	claims.setAudience(conf.getAudience()); // to whom the token is intended to be sent
	claims.setExpirationTimeMinutesInTheFuture(conf.getTokenExpiration()); // time when the token will expire
	claims.setJwtId(UUID.randomUUID().toString());
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
}
