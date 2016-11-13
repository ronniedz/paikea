package cab.bean.srvcs.tube4kids.resources;

import java.util.HashMap;

/**
 * A wrapper for HashMap to add convenience, fluent, method.
 * 
 * @author ronalddennison
 *
 */
public class QVal extends HashMap<String, String> {
	
	private static final long serialVersionUID = 4080437368309350655L;

	public QVal() {
	    super();
	}
	
	/**
	 * A fluent version of<code>put()</code>.
	 * 
	 * @param key	a string key
	 * @param val	a string value
	 * @return		self
	 */
	public QVal xput(String key, String val) {
	    put(key, val);
	    return this;
	}
}
