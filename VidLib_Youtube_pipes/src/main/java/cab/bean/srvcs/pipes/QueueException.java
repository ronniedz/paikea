package cab.bean.srvcs.pipes;

import javax.ws.rs.ProcessingException;

public class QueueException extends ProcessingException {

    /**
     * Generate VersionID
     */
    private static final long serialVersionUID = 1L;

    public QueueException(String message) {
	super(message);
    }
}
