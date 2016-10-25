package cab.bean.srvcs.tube4kids.api;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

// JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
// JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode
@Accessors(fluent = true)
public class ErrorBox {
    //
    @JsonProperty
    List<Error> errors;

    @JsonProperty
    private int code;

    @JsonProperty
    private String message;
}
