package cab.bean.srvcs.tube4kids.core;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import cab.bean.srvcs.tube4kids.utils.StringTool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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
	this.blocked = StringTool.join(blockedList, ",", false);
    }

    @JsonSetter("allowed")
    public void setAllowedList(List<String> allowedList) {
	this.allowed = StringTool.join(allowedList, ",", false);
    }

}
