package commons;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    public static void deleteUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("UserManager: Attempted to delete user with null or empty username.");
            return;
        }

        User userToDelete = new User(username);
        Map<String, Object> params = new HashMap<String, Object>();

        UserEventDispatcher.fireUserDeleting(userToDelete, params);
        
    }
}