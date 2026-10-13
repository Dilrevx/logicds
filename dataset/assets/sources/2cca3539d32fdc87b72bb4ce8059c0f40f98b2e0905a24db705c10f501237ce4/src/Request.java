import java.util.HashMap;
import java.util.Map;

public class Request {

    public String method = "GET";
    public Map<String, String> form = new HashMap<String, String>();

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Map<String, String> getForm() { return form; }
}
