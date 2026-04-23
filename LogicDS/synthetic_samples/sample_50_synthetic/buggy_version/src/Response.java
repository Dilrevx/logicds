import java.util.HashMap;
import java.util.Map;

public class Response {

    public static final int STATUS_OK = 200;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_INTERNAL_ERROR = 500;

    private int status = STATUS_OK;
    private String body = "";
    private final Map<String, String> header = new HashMap<String, String>();

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Map<String, String> getHeader() { return header; }
}
