package decaf;

/**
 * Location of tokens.
 */
public class Location implements Comparable<Location> {

    public static final Location NO_LOCATION = new Location(-1, -1);

    /**
     * Line number.
     */
    private int line;

    /**
     * Column number.
     */
    private int column;

    public Location(int lin, int col) {
        line = lin;
        column = col;
    }

    @Override
    public String toString() {
        return "(" + line + "," + column + ")";
    }

    public int compareTo(Location o) {
        if (line > o.line) {
            return 1;
        }
        if (line < o.line) {
            return -1;
        }
        if (column > o.column) {
            return 1;
        }
        if (column < o.column) {
            return -1;
        }
        return 0;
    }
}
