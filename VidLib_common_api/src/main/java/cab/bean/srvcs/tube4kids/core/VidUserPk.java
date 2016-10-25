package cab.bean.srvcs.tube4kids.core;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@ToString
//EqualsAndHashCode(exclude = { "genreId", "genre2Id" })
@Getter
@Setter
public class VidUserPk implements Serializable {
    private static final long serialVersionUID = 490200883975165432L;

    @ManyToOne
    private RelVideo video;

    @ManyToOne
    private User user;

    @Override
    public boolean equals(Object o) {
	if ((o != null) && o.getClass().equals(this.getClass())) {
	    VidUserPk  that = ((VidUserPk) o);
	    return
		    (this.user != null ? that.getUser().equals(this.user) : false) &&
		    (this.video != null ? that.getVideo().equals(this.video) : false);
	}
	return false;
    }

    public int hashCode() {
        return 31 * (
        		((this.user != null) ? this.user.hashCode() : 0)  +
        		((this.video != null) ? this.video.hashCode() : 0)
        	);
    }

}