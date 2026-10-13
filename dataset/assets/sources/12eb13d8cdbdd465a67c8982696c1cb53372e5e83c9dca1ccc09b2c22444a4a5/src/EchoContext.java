import java.util.HashMap;
import java.util.Map;

public class EchoContext {

    public static final String COOKIE_MISSING = "__MISSING__";

    private String path;
    private String method;
    private String accessTokenHeader = "";
    private final Map<String, String> cookies = new HashMap<String, String>();
    private final Map<String, Object> attrs = new HashMap<String, Object>();

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getAccessTokenHeader() { return accessTokenHeader; }
    public void setAccessTokenHeader(String accessTokenHeader) { this.accessTokenHeader = accessTokenHeader; }

    public String getCookie(String name) {
        String v = cookies.get(name);
        return v == null ? COOKIE_MISSING : v;
    }

    public void putCookie(String name, String value) {
        cookies.put(name, value);
    }

    public Object getAttr(String key) { return attrs.get(key); }
    public void setAttr(String key, Object value) { attrs.put(key, value); }
}
