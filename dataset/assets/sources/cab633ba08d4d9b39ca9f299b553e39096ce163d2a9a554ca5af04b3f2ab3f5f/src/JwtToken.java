import java.util.HashMap;
import java.util.Map;

public class JwtToken {

    private boolean valid;
    private final Map<String, Object> header = new HashMap<String, Object>();
    private String methodAlg = "HS256";

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Map<String, Object> getHeader() { return header; }

    public String getMethodAlg() { return methodAlg; }
    public void setMethodAlg(String methodAlg) { this.methodAlg = methodAlg; }
}
