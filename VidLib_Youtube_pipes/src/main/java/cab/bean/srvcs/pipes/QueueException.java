package cab.bean.srvcs.pipes;

import javax.ws.rs.ProcessingException;

public class QueueException extends ProcessingException {

    public QueueException(String message) {
	super(message);
    }
}
