package commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserEventDispatcher {
    private static List<UserEventListener> listeners = new ArrayList<UserEventListener>();

    public static synchronized void addListener(UserEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public static synchronized void removeListener(UserEventListener listener) {
        listeners.remove(listener);
    }

    public static synchronized void fireUserDeleting(User user, Map<String, Object> params) {
        for (UserEventListener listener : new ArrayList<UserEventListener>(listeners)) {
            listener.userDeleting(user, params);
        }
    }
    
    public static synchronized void fireUserCreated(User user, Map<String, Object> params) {
        for (UserEventListener listener : new ArrayList<UserEventListener>(listeners)) {
            listener.userCreated(user, params);
        }
    }
    
    public static synchronized void clearListeners() {
        listeners.clear();
    }
}