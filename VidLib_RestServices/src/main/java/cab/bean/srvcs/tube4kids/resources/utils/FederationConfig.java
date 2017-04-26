package cab.bean.srvcs.tube4kids.resources.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.VerificationJwkSelector;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.HmacKey;
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.jose4j.lang.UnresolvableKeyException;

import cab.bean.srvcs.tube4kids.exception.ConfigurationException;

public abstract class FederationConfig {

    @NotNull
    protected String[] audience;

    @NotNull
    protected String[] issuer;

    @NotNull
    protected String algorithmIdentifier = org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256; // "HS256"

    protected int tokenExpiration;
    protected String tokenType = "Bearer";
    protected String jwtIdPrefix;
    protected int allowedSkew;
    protected Key verificationKey;
    protected Key publicKey;
    protected VerificationKeyResolver signatureVerificationKeyResolver;
    protected String signatureKeyURL;
    protected HttpsJwks httpsJWKS;

    // For web and browser
    protected int cookieMaxAge;
    protected String cookieName;
    protected boolean cookieSecure = false;
    protected boolean cookieHttpOnly = false;

    protected String realmName;

    protected AlgorithmConstraints jwsAlgorithmConstraints;


//    public VerificationKeyResolver refreshSignatureKeyResolver() throws Exception {
//	if (this.httpsJWKS == null) {
//	    throw new Exception("httpsJWKS Not Set");
//	}
//	httpsJWKS.refresh();
//	signatureVerificationKeyResolver = new JwksVerificationKeyResolver(httpsJWKS.getJsonWebKeys());
//	return signatureVerificationKeyResolver;
//    }
    
    public VerificationKeyResolver getSignatureVerificationKeyResolver() throws JoseException, IOException {
	if (this.httpsJWKS != null) {
	    signatureVerificationKeyResolver = new RefreshedResolver(httpsJWKS);
	}
	return signatureVerificationKeyResolver;
    }

    /**
     * @param signatureKeyURL the signatureKeyURL to set
     */
    public FederationConfig setSignatureKeyURL(String signatureKeyURL) {
        this.signatureKeyURL = signatureKeyURL;
        this.httpsJWKS = new HttpsJwks(signatureKeyURL);
	return this;
    }

    public JwtConsumerBuilder getConsumer() throws JoseException, IOException, ConfigurationException  {
	JwtConsumerBuilder builder = new JwtConsumerBuilder()
	.setExpectedAudience(getAudience())
	.setExpectedIssuers(true, getIssuer())
	
	// the JWT must have an expiration time
	.setRequireExpirationTime()
	// the JWT must have a subject claim
	.setRequireSubject()
	// relaxes key length requirement
	.setRelaxVerificationKeyValidation()
	// allow some leeway in validating time based claims to account for clock skew
	.setAllowedClockSkewInSeconds(getAllowedSkew());

	if (this.jwsAlgorithmConstraints != null) {
	    builder.setJwsAlgorithmConstraints(getAlgorithmConstraints()); 
	}
	
	Key v = null;
	VerificationKeyResolver vkr = null;;
	
            	if ((v = getVerificationKey()) != null) {
            		builder.setVerificationKey(v); 
            	}
            	else if ((vkr = getSignatureVerificationKeyResolver()) != null) {
		    // verify the signature with the public key
		    builder.setVerificationKeyResolver(vkr);
		}
		else {
		    throw new ConfigurationException("On of either the verificationKey or signatureVerificationKeyResolver must be set");
		}
	return builder;
    }

    

    /**
     * @return the publicKey
     */
    public Key getPublicKey() {
        return publicKey;
    }

    /**
     * @param publicKey the publicKey to set
     */
    public FederationConfig setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
	return this;
    }

    /**
     * @return the audience
     */
    public String[] getAudience() {
	return audience;
    }

    /**
     * @param audience
     *            the audience to set
     */
    public FederationConfig setAudience(String... audience) {
	this.audience = audience;
	return this;
    }

    /**
     * Defaults to 'Bearer'.
     * 
     * @return the type of token
     */
    public String getTokenType() {
	return tokenType;
    }

    /**
     * @param tokenType
     *            the tokenType to set
     */
    public FederationConfig setTokenType(String tokenType) {
	this.tokenType = tokenType;
	return this;
    }

    /**
     * @return the issuer
     */
    public String[] getIssuer() {
	return issuer;
    }

    /**
     * @param issuer
     *            the issuer to set
     */
    public FederationConfig setIssuer(String[] issuer) {
	this.issuer = issuer;
	return this;
    }

    /**
     * @return the jwtIdPrefix
     */
    public String getJwtIdPrefix() {
	return jwtIdPrefix;
    }

    /**
     * @param jwtIdPrefix
     *            the jwtIdPrefix to set
     */
    public FederationConfig setJwtIdPrefix(String jwtIdPrefix) {
	this.jwtIdPrefix = jwtIdPrefix;
	return this;
    }

    /**
     * @return the algorithmIdentifier
     */
    public String getAlgorithmIdentifier() {
	return algorithmIdentifier;
    }

    /**
     * @param algorithmIdentifier
     *            the algorithmIdentifier to set
     */
    public FederationConfig setAlgorithmIdentifier(String algorithmIdentifier) {
	this.algorithmIdentifier = algorithmIdentifier;
	return this;
    }

    /**
     * @return the allowedSkew
     */
    public int getAllowedSkew() {
	return allowedSkew;
    }

    /**
     * @param allowedSkew
     *            the allowedSkew to set
     */
    public FederationConfig setAllowedSkew(int allowedSkew) {
	this.allowedSkew = allowedSkew;
	return this;
    }

    /**
     * @return the tokenExpiration
     */
    public int getTokenExpiration() {
	return tokenExpiration;
    }

    /**
     * @param tokenExpiration
     *            the tokenExpiration to set
     */
    public FederationConfig setTokenExpiration(int tokenExpiration) {
	this.tokenExpiration = tokenExpiration;
	return this;
    }

    /**
     * @return the verificationKey
     * @throws Exception
     */
    public FederationConfig setVerificationKey(String keyValue) throws UnsupportedEncodingException {
	setVerificationKey(new HmacKey(keyValue.getBytes("UTF-8")));
	return this;
    }

    public Key getVerificationKey() {
	return this.verificationKey;
    }

    /**
     * @param verificationKey
     *            the verificationKey to set
     */
    public FederationConfig setVerificationKey(Key verificationKey) {
	this.verificationKey = verificationKey;
	return this;
    }

    /**
     * @return the cookieMaxAge
     */
    public int getCookieMaxAge() {
	return cookieMaxAge;
    }

    /**
     * @param cookieMaxAge
     *            the cookieMaxAge to set
     */
    public FederationConfig setCookieMaxAge(int cookieMaxAge) {
	this.cookieMaxAge = cookieMaxAge;
	return this;
    }

    /**
     * @return the cookieName
     */
    public String getCookieName() {
	return cookieName;
    }

    /**
     * @param cookieName
     *            the cookieName to set
     */
    public FederationConfig setCookieName(String cookieName) {
	this.cookieName = cookieName;
	return this;
    }

    /**
     * @return the cookieSecure
     */
    public boolean isCookieSecure() {
	return cookieSecure;
    }

    /**
     * @param cookieSecure
     *            the cookieSecure to set
     */
    public FederationConfig setCookieSecure(boolean cookieSecure) {
	this.cookieSecure = cookieSecure;
	return this;
    }

    /**
     * @return the cookieHttpOnly
     */
    public boolean isCookieHttpOnly() {
	return cookieHttpOnly;
    }

    /**
     * @param cookieHttpOnly
     *            the cookieHttpOnly to set
     */
    public FederationConfig setCookieHttpOnly(boolean cookieHttpOnly) {
	this.cookieHttpOnly = cookieHttpOnly;
	return this;
    }

    /**
     * @return the realmName
     */
    public String getRealmName() {
	return realmName;
    }

    /**
     * @param realmName
     *            the realmName to set
     */
    public FederationConfig setRealmName(String realmName) {
	this.realmName = realmName;
	return this;
    }

    public boolean getRequireSubject() {
	return true;
    }

    public AlgorithmConstraints getAlgorithmConstraints() {
	return jwsAlgorithmConstraints;
    }

    /**
     * @return the signatureKeyURL
     */
    public String getSignatureKeyURL() {
        return signatureKeyURL;
    }

    /**
     * @return the httpsJWKS
     */
    public HttpsJwks getHttpsJWKS() {
        return httpsJWKS;
    }

    /**
     * @param httpsJWKS the httpsJWKS to set
     */
    public FederationConfig setHttpsJWKS(HttpsJwks httpsJWKS) {
        this.httpsJWKS = httpsJWKS;
	return this;
    }

    
//  RSAPrivateKey privateKey = getPrivateKey(keyStore, keyStorePassword, alias);


////////////////////////////////////////
//  public abstract boolean getEnableRequireEncryption();
//  public abstract boolean getDisableRequireSignature();
//  public abstract boolean getEnableLiberalContentTypeHandling();
//  public abstract boolean getSkipSignatureVerification();
//  public abstract boolean getSkipAllValidators();
//  public abstract boolean getSkipAllDefaultValidators();
//  public abstract AlgorithmConstraints getJwsAlgorithmConstraints();
//  public abstract AlgorithmConstraints getJweAlgorithmConstraints();
//  public abstract AlgorithmConstraints getJweContentEncryptionAlgorithmConstraints();
//  public abstract Key getDecryptionKey();

//  public abstract DecryptionKeyResolver getDecryptionKeyResolver();
//  public abstract boolean getSkipDefaultAudienceValidation();
//
//  public abstract boolean getRequireJwtId();
//  public abstract boolean getRequireExpirationTime();
//  public abstract boolean getRequireIssuedAt();
//  public abstract boolean getRequireNotBefore();
//  public abstract NumericDate getEvaluationTime();
//  public abstract int getAllowedClockSkewInSeconds();
//  public abstract int getMaxFutureValidityInMinutes();
//  public abstract boolean getRelaxVerificationKeyValidation();
//  public abstract String getRelaxDecryptionKeyValidation();
  
//  public abstract JwsCustomizer getJwsCustomizer();
//  public abstract JweCustomizer getJweCustomizer();
//  public abstract ProviderContext getJwsProviderContext();
//  public abstract ProviderContext getJweProviderContext();
}
