public class RegexUtil {

    private static final String SPECIAL_CHARS = "\\^$.|?*+(){}[]";

    public static String escape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (SPECIAL_CHARS.indexOf(c) >= 0) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private RegexUtil() {}
}
