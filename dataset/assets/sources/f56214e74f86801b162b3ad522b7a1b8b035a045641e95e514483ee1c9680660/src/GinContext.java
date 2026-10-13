import java.util.HashMap;
import java.util.Map;

public class GinContext {

    private String token = "";
    private boolean aborted = false;
    private boolean nextCalled = false;
    private AuthError responseError;
    private final Map<String, Object> attrs = new HashMap<String, Object>();

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public boolean isAborted() { return aborted; }
    public void abort() { this.aborted = true; }

    public boolean isNextCalled() { return nextCalled; }
    public void next() { this.nextCalled = true; }

    public AuthError getResponseError() { return responseError; }
    public void setResponseError(AuthError responseError) { this.responseError = responseError; }

    public void set(String key, Object value) { attrs.put(key, value); }
    public Object get(String key) { return attrs.get(key); }
}
