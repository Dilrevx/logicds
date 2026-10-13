import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigParser {

    private final Map<String, String> defaults;
    private final Map<String, Map<String, String>> sections = new LinkedHashMap<String, Map<String, String>>();

    public ConfigParser(Map<String, String> defaults) {
        this.defaults = new HashMap<String, String>(defaults);
    }

    public void readFp(String content) {
        String currentSection = null;
        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                currentSection = line.substring(1, line.length() - 1);
                sections.put(currentSection, new LinkedHashMap<String, String>());
            } else if (currentSection != null && line.contains("=")) {
                int idx = line.indexOf('=');
                String k = line.substring(0, idx).trim();
                String v = line.substring(idx + 1).trim();
                sections.get(currentSection).put(k, v);
            }
        }
    }

    public boolean read(String filename) {
        return false;
    }

    public List<String> sections() {
        return new ArrayList<String>(sections.keySet());
    }

    public String get(String section, String key) {
        Map<String, String> s = sections.get(section);
        if (s == null) return null;
        String value = s.get(key);
        if (value == null) return null;
        for (Map.Entry<String, String> e : defaults.entrySet()) {
            value = value.replace("%(" + e.getKey() + ")s", e.getValue());
        }
        return value;
    }
}
