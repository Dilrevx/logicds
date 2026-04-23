import java.util.HashMap;
import java.util.Map;

public class Server {

    private boolean skipAuth;
    private final Map<Integer, Object> users = new HashMap<Integer, Object>();

    public boolean defaultAuthSkipper(EchoContext c) {
        return skipAuth;
    }

    public void setSkipAuth(boolean skipAuth) {
        this.skipAuth = skipAuth;
    }

    public Object getUser(int userId) {
        return users.get(userId);
    }

    public void putUser(int userId, Object user) {
        users.put(userId, user);
    }
}
