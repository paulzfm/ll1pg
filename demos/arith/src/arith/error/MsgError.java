package arith.error;

import arith.Location;

/**
 * Message error, used by parser.
 */
public class MsgError extends CompileError {

	private String msg;

	public MsgError(Location location, String msg) {
		super(location);
		this.msg = msg;
	}

	@Override
	protected String getErrMsg() {
		return msg;
	}

}
