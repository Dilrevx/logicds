import java.util.HashMap;
import java.util.Map;

public class AuthCache {

    public static final class GetResult {
        public final boolean value;
        public final boolean present;
        public GetResult(boolean value, boolean present) {
            this.value = value;
            this.present = present;
        }
    }

    private final Map<String, Boolean> store = new HashMap<String, Boolean>();

    public GetResult get(String key) {
        Boolean v = store.get(key);
        if (v == null) {
            return new GetResult(false, false);
        }
        return new GetResult(v, true);
    }

    public void set(String key, boolean value) {
        store.put(key, value);
    }

    public Boolean peek(String key) {
        return store.get(key);
    }

    public void clear() {
        store.clear();
    }
}
