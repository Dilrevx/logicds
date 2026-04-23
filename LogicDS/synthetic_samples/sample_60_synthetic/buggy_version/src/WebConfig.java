import java.util.HashMap;
import java.util.Map;

public class WebConfig {

    public static final class HttpConfig {
        public final Map<String, String> header = new HashMap<String, String>();
    }

    public final HttpConfig httpConfig = new HttpConfig();
    public final Map<String, String> users = new HashMap<String, String>();

    public HttpConfig getHttpConfig() { return httpConfig; }
    public Map<String, String> getUsers() { return users; }
}
