import java.util.HashMap;
import java.util.Map;

public class UserRegistry {

    private static final Map<String, MyUser> byUid = new HashMap<String, MyUser>();

    public static void put(String uid, MyUser user) {
        byUid.put(uid, user);
    }

    public static MyUser myuserFindUid(String uid) {
        if (uid == null || uid.isEmpty()) {
            return null;
        }
        return byUid.get(uid);
    }

    public static void reset() {
        byUid.clear();
    }

    private UserRegistry() {}
}
