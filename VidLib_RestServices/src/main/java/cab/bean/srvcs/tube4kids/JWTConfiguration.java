package cab.bean.srvcs.tube4kids;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cab.bean.srvcs.tube4kids.resources.utils.FederationConfig;

//@ToString
public class JWTConfiguration extends FederationConfig {

//    private static final Logger LOGGER = LoggerFactory.getLogger(JWTConfiguration.class);

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

