import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private int status = HttpStatus.OK;
    private String body = "";
    private final Map<String, String> headers = new HashMap<String, String>();

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Map<String, String> getHeaders() { return headers; }
}
