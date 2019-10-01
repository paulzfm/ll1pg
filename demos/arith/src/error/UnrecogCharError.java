package arith.error;

import arith.Location;

/**
 * Unrecognize character.
 */
public class UnrecogCharError extends CompileError {

	private char c;

	public UnrecogCharError(Location location, char c) {
		super(location);
		this.c = c;
	}

	@Override
	protected String getErrMsg() {
		return "unrecognized character '" + c + "'";
	}
}
