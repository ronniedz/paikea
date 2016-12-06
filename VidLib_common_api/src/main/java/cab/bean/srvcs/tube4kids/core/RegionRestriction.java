package cab.bean.srvcs.tube4kids.core;

import java.util.List;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.ALWAYS)
public class RegionRestriction {
    
    @Column
    public String allowed = null;

    @Column
    public  String blocked = null;
    
    public boolean isPermittedIn(String region) {
	return (
	    (allowed != null) ? allowed.contains(region) : true  
	    &&
	    (blocked == null) ? true : ! blocked.contains(region)
	);
    }

    @JsonProperty
    public String getAllowed() {
        return allowed;
    }

    @JsonProperty
    public String getBlocked() {
        return blocked;
    }
    
    public void setAllowed(String allowed) {
	this.allowed = allowed;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    @JsonSetter("blocked")
    public void setBlockedList(List<String> blockedList) {
	final StringJoiner joiner = new StringJoiner(",");
	blockedList.forEach(x -> { joiner.add(x); });
	this.blocked =  joiner.toString();

    }

    @JsonSetter("allowed")
    public void setAllowedList(List<String> allowedList) {
	final StringJoiner joiner = new StringJoiner(",");
	allowedList.forEach(x -> { joiner.add(x); });
	this.allowed =  joiner.toString();

    }
   
}
