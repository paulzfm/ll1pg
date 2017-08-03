package decaf.error;

import decaf.Location;

/**
 * Compilation error.
 */
public abstract class DecafError extends Exception {

	/**
	 * Where error happens.
	 */
	protected Location location;

	/**
	 * @return description of error.
	 */
	protected abstract String getErrMsg();

	public DecafError(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * @return complete description of error including location.
	 */
	@Override
	public String toString() {
		if (location.equals(Location.NO_LOCATION)) {
			return "*** Error: " + getErrMsg();
		} else {
			return "*** Error at " + location + ": " + getErrMsg();
		}
	}

}
