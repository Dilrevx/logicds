import java.util.HashMap;
import java.util.Map;

public class Env {

    private static final Map<String, String> vars = new HashMap<String, String>();

    public static String getenv(String name) {
        String v = vars.get(name);
        return v == null ? "" : v;
    }

    public static void setenv(String name, String value) {
        vars.put(name, value);
    }

    public static void reset() {
        vars.clear();
    }

    private Env() {}
}
