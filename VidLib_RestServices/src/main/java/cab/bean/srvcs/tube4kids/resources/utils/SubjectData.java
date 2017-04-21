package cab.bean.srvcs.tube4kids.resources.utils;

import java.util.HashMap;
import java.util.Map;

/*
at_hash=iHeHF4AgkgNWaZp4RoP8dA,
sub=113488468913948362093,
email_verified=true,
iss=accounts.google.com,
given_name=Ronald,
locale=en,
picture=https://lh6.googleusercontent.com/-b9ZEXclgcwI/AAAAAAAAAAI/AAAAAAAAAeo/ZbMQxei5DAg/s96-c/photo.jpg,
aud=16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com,
azp=16943376142-23682cd11vmd29jg91q5hg2r5g9bd6b8.apps.googleusercontent.com,
name=Ronald Dennison,
exp=1492734383,
iat=1492730783,
family_name=Dennison,
email=ronniedz@gmail.com

 */

public class SubjectData extends HashMap<String, Object> {
    private static final long serialVersionUID = 2114523776122688555L;

    public SubjectData(Map<String, Object> claimsMap) {
	claimsMap.entrySet().forEach(entry -> { 
	    switch (entry.getKey()) {
	    	case "sub" : {
	    	    this.put("subject", entry.getValue());
	    	    break;
	    	}
	    	case "aud" : {
	    	    this.put("audience", entry.getValue());
	    	    break;
	    	}

	    	case "iss" : {
	    	    this.put("issuer", entry.getValue());
	    	    break;
	    	}

	    	case "family_name" : {
	    	    this.put("lastname", entry.getValue());
	    	    break;
	    	}

	    	case "given_name" : {
	    	    this.put("firstname", entry.getValue());
	    	    break;
	    	}

	    	case "iat" : {
	    	    this.put("issued_at", entry.getValue());
	    	    break;
	    	}
	    	
	    	case "exp" : {
	    	    this.put("expires", entry.getValue());
	    	    break;
	    	}

	    	default : this.put(entry.getKey(), entry.getValue());
	    };
	});
    }

    public String getSubject() {
	return (String) get("subject");
    }
    
    public String getEmail() {
	return (String) get("email");
    }
    
    public String getEmailVerified() {
	return (String) get("email_verified");
    }

    public String getFirstname() {
	return (String) get("firstname");
    }
    
    public String getLastname() {
	return (String) get("lastname");
    }

    public String getLocale() {
	return (String) get("locale");
    }

    public String getPicture() {
	return (String) get("picture");
    }
    
    public String getAudience() {
	return (String) get("audience");
    }

    public String getExpirationTime() {
	return (String) get("expirationTime");
    }

    public String getNotBefore() {
	return (String) get("notBefore");
    }

    public String getIssuedAt() {
	return (String) get("issueAt");
    }

    public String getIssuer() {
	return (String) get("issuer");
    }

    public String getJwtId() {
	return (String) get("jwtId");
    }

    
    // Delete from final copy
    public String removeIssuer() {
	return (String) remove("issuer");
    }

    public Map<String, Object> getClaims() {
	return this;
    }
}

