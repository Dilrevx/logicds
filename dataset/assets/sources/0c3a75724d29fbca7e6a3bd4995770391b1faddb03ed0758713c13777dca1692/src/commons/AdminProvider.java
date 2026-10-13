package commons;

import java.util.List;

public interface AdminProvider {
    List<JID> getAdmins();
    void setAdmins(List<JID> admins);
}