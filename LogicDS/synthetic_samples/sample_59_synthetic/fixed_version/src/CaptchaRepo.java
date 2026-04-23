import java.util.HashMap;
import java.util.Map;

public class CaptchaRepo {

    public static final class CaptchaResult {
        public final String value;
        public final Exception err;
        public CaptchaResult(String value, Exception err) {
            this.value = value;
            this.err = err;
        }
    }

    private final Map<String, String> store = new HashMap<String, String>();
    private int getCalls;
    private int delCalls;

    public void put(String key, String value) {
        store.put(key, value);
    }

    public CaptchaResult getCaptcha(Context ctx, String key) {
        getCalls++;
        String value = store.get(key);
        if (value == null) {
            return new CaptchaResult("", new Exception("captcha not found for key " + key));
        }
        return new CaptchaResult(value, null);
    }

    public Exception delCaptcha(Context ctx, String key) {
        delCalls++;
        store.remove(key);
        return null;
    }

    public int getGetCalls() { return getCalls; }
    public int getDelCalls() { return delCalls; }
    public boolean has(String key) { return store.containsKey(key); }
}
