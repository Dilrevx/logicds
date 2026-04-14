package commons;

import java.util.Map;

public interface UserEventListener {
    void userCreated(User user, Map<String, Object> params);
    void userDeleting(User user, Map<String, Object> params);
    void userModified(User user, Map<String, Object> params);
}