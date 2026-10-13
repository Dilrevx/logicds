import java.util.ArrayList;
import java.util.List;

public class AuthConfig {

    public int protect = 1;
    public boolean allowBasic = true;
    public final List<String> authorizedPrincipals = new ArrayList<String>();

    public int getProtect() { return protect; }
    public void setProtect(int protect) { this.protect = protect; }

    public boolean isAllowBasic() { return allowBasic; }
    public void setAllowBasic(boolean allowBasic) { this.allowBasic = allowBasic; }

    public List<String> getAuthorizedPrincipals() { return authorizedPrincipals; }
}
