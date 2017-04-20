/*
 * Copyright 2012-2016 Brian Campbell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cab.bean.srvcs.tube4kids.resources.utils;

import java.util.Map;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;

/**
 *
 */
public abstract class aTokenService
{
    
    interface SubjectData {
	String getSubject();
	Map<String, Object> getClaims();
    }

    public final JwtConsumer jwtRrelaxedConsumer;
   
    public aTokenService() {
	this.jwtRrelaxedConsumer = new JwtConsumerBuilder()
	    .setSkipAllValidators()
	    .setDisableRequireSignature()
	    .setSkipSignatureVerification()
	    .build();
    }
    
    public abstract JwtClaims parse(String tokenString) throws InvalidJwtException;
    
    public abstract JwtContext verify(String tokenString, FederationConfig conf) throws InvalidJwtException;

    public abstract String generate(SubjectData data, FederationConfig conf);
    
//    public abstract JwtContext verify(JwtConsumer jwtConsumer, String tokenString) throws InvalidJwtException;
    
//    public abstract  Map<String, Object> extractVerifiedValues(String tokenString, FederationConfig conf) throws InvalidJwtException;
//    
//    public abstract  Map<String, Object> extractValues(String tokenString) throws InvalidJwtException;

//    public abstract JwtContext refresh(String tokenString, FederationConfig conf);
    
}


