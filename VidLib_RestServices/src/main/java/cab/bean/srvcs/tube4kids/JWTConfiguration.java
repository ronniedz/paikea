package cab.bean.srvcs.tube4kids;

import java.io.UnsupportedEncodingException;

import org.jose4j.keys.HmacKey;

import cab.bean.srvcs.tube4kids.resources.utils.FederationConfig;

public class JWTConfiguration extends FederationConfig {

   /**
     * @return the verificationKey
     * @throws Exception 
     */
    @Override
   public JWTConfiguration setVerificationKey(String keyValue) throws UnsupportedEncodingException {
       setVerificationKey(new HmacKey(keyValue.getBytes("UTF-8")));
       return this;
   }


   /**
    * @param publicKey the publicKey to set
    */
   public FederationConfig setPublicKey(String keyValue)  throws UnsupportedEncodingException {
       setPublicKey(new HmacKey(keyValue.getBytes("UTF-8")));
       return this;
   }
}
