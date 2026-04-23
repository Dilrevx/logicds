import java.util.HashMap;
import java.util.Map;

public class DefinedRights {

    private static final Map<String, String> rights = new HashMap<String, String>();

    static {
        rights.put("owner_write",
                "[own]\n" +
                "user = .+\n" +
                "collection = /%(login)s/.*\n" +
                "permission = rw\n");
    }

    public static boolean has(String type) {
        return rights.containsKey(type);
    }

    public static String get(String type) {
        return rights.get(type);
    }

    private DefinedRights() {}
}
