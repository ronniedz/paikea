package cab.bean.srvcs.tube4kids.api;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "token")
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@EqualsAndHashCode
public class Token {

    @Column
    private Long userId;

    @Id
    private UUID token;

    public Token(Long userId, UUID token) {
	this.userId = userId;
	this.token = token;
    }

    public Long getUserId() {
	return userId;
    }

    public void setUserId(Long userId) {
	this.userId = userId;
    }

    public UUID getToken() {
	return token;
    }

    public void setToken(UUID token) {
	this.token = token;
    }
}