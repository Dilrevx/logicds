import java.util.ArrayList;
import java.util.List;

public class MyUser {

    public String name = "";
    public long lastLogin;
    public final List<User> logins = new ArrayList<User>();
    public boolean soper;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }

    public List<User> getLogins() { return logins; }

    public boolean isSoper() { return soper; }
    public void setSoper(boolean soper) { this.soper = soper; }
}
