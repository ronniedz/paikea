package cab.bean.srvcs.tube4kids.resources;

import java.util.HashMap;

public class QVal extends HashMap<String, String> {
	
	private static final long serialVersionUID = 4080437368309350655L;

	public QVal() {
	    super();
	}
	public QVal xput(String key, String val) {
	    put(key, val);
	    return this;
	}
}

