package cab.bean.srvcs.tube4kids;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import lombok.ToString;

import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ToString
public class JWTConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTConfiguration.class);

    private String audienceId = "client.apps.beancabfamilyuser.com";
    private String authHeaderPrefix = "Bearer";
    private String cookieName = "beancab";
    private String issuer = "apps.beancab.com";
    private String keyId = "beancab.com_rsapublic";
    private String realmName = "Children's Video Library";
    private String signatureAlgorithm = org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256; //	"HS256"
    private boolean secure = false;
    private boolean httpOnly = false;
    private int clockSkew = 30; // in seconds
    private int maxAge = 3600;
    private int tokenExpiration = 1440; // in minutes
    private Key verificationKey ;

    /**
     * @return the audienceId
     */
    public String getAudienceId() {
        return audienceId;
    }

   /**
     * @param audienceId the audienceId to set
     */
    public void setAudienceId(String audienceId) {
        this.audienceId = audienceId;
    }

   /**
     * @return the authHeaderPrefix
     */
    public String getAuthHeaderPrefix() {
        return authHeaderPrefix;
    }

   /**
     * @param authHeaderPrefix the authHeaderPrefix to set
     */
    public void setAuthHeaderPrefix(String authHeaderPrefix) {
        this.authHeaderPrefix = authHeaderPrefix;
    }

   /**
     * @return the cookieName
     */
    public String getCookieName() {
        return cookieName;
    }

   /**
     * @param cookieName the cookieName to set
     */
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

   /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

   /**
     * @param issuer the issuer to set
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

   /**
     * @return the keyId
     */
    public String getKeyId() {
        return keyId;
    }

   /**
     * @param keyId the keyId to set
     */
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

   /**
     * @return the realmName
     */
    public String getRealmName() {
        return realmName;
    }

   /**
     * @param realmName the realmName to set
     */
    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

   /**
     * @return the signatureAlgorithm
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

   /**
     * @param signatureAlgorithm the signatureAlgorithm to set
     */
    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

   /**
     * @return the verificationKey
 * @throws Exception 
     */
    public void setVerificationKey(String keyValue) throws UnsupportedEncodingException {
	try {
	    verificationKey =  new HmacKey(keyValue.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException e) {
	    LOGGER.error("HMAC failed");
	    LOGGER.error("exception: {}", e.getMessage());
	    e.printStackTrace();
	    throw e;
	}
    }

   /**
     * @return the verificationKey
     */
    public Key getVerificationKey() {
        return verificationKey;
    }

   /**
     * @param verificationKey the verificationKey to set
     */
    public void setVerificationKey(Key verificationKey) {
        this.verificationKey = verificationKey;
    }

    /**
     * @return the secure
     */
    public boolean isSecure() {
        return secure;
    }
    
    /**
     * @param secure the secure to set
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }
    
    /**
     * @return the httpOnly
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }
    
    /**
     * @param httpOnly the httpOnly to set
     */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
    
    /**
     * @return the clockSkew
     */
    public int getClockSkew() {
        return clockSkew;
    }
    
    /**
     * @param clockSkew the clockSkew to set
     */
    public void setClockSkew(int clockSkew) {
        this.clockSkew = clockSkew;
    }
    
    /**
     * @return the maxAge
     */
    public int getMaxAge() {
        return maxAge;
    }
    
    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }
    
    /**
     * Expiry in minutes
     * @return the tokenExpiration
     */
    public int getTokenExpiration() {
        return tokenExpiration;
    }
    
    /**
     * @param tokenExpiration the tokenExpiration to set
     */
    public void setTokenExpiration(int tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }
    
    



    
}
