package decaf.utils;

public final class MiscUtils {
    /**
     * Return a string with quoted charactors.
     *
     * @param str input string (internal presentation)
     * @return string with quoted charactors and `"` (external presentation)
     */
    public static String quote(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(c);
            }
        }
        return ('"' + sb.toString() + '"');
    }
}
