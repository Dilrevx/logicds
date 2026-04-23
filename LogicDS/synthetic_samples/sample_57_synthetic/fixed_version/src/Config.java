import java.util.HashMap;
import java.util.Map;

public class Config {

    private static final Map<String, Map<String, String>> store = new HashMap<String, Map<String, String>>();

    public static String get(String section, String key) {
        Map<String, String> s = store.get(section);
        return s == null ? "" : s.getOrDefault(key, "");
    }

    public static void set(String section, String key, String value) {
        Map<String, String> s = store.get(section);
        if (s == null) {
            s = new HashMap<String, String>();
            store.put(section, s);
        }
        s.put(key, value);
    }

    public static void reset() {
        store.clear();
    }

    private Config() {}
}
