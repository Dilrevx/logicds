import java.util.ArrayList;
import java.util.List;

public class VerifyAuthResult {

    public boolean isBasicAuth;
    public String username = "";
    public String name = "";
    public List<String> groups = new ArrayList<String>();
    public List<String> emails = new ArrayList<String>();
    public int authLevel;
    public Exception err;
}
