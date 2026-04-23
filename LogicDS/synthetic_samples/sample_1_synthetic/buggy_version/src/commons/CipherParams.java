package commons;

import java.util.HashMap;
import java.util.Map;

public class CipherParams {
    private Map<String, Integer> params = new HashMap<>();

    public void set(String key, int value) {
        params.put(key, value);
    }

    public Integer get(String key) {
        return params.get(key);
    }

    public boolean contains(String key) {
        return params.containsKey(key);
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }

    public Map<String, Integer> getAllParams() {
        return params;
    }
}

