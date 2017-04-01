package cab.bean.srvcs.tube4kids;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import org.jose4j.keys.HmacKey;


public class JWTConfiguration {

    private String audienceId = "client.apps.beancabfamilyuser.com";
    private String authHeaderPrefix = "Bearer";
    private String cookieName = "beancab";
    private String issuer = "apps.beancab.com";
    private String keyId = "beancab.com_rsapublic";
    private String realmName = "Children's Video Library";
    private String signatureAlgorithm = org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256; //	"HS256"
    private Key verificationKey = null;
    
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
     */
    public void setVerificationKey(String keyValue) throws UnsupportedEncodingException {
	verificationKey =  new HmacKey(keyValue.getBytes("UTF-8"));
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
    


    
}
