package commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class DefaultAdminProvider implements AdminProvider {
    private List<JID> adminJIDs = new ArrayList<JID>();

    public DefaultAdminProvider() {
    }

    @Override
    public List<JID> getAdmins() {
        return Collections.unmodifiableList(new ArrayList<JID>(adminJIDs));
    }

    @Override
    public void setAdmins(List<JID> admins) {
        this.adminJIDs.clear();
        if (admins != null) {
            for (JID jid : admins) {
                this.adminJIDs.add(jid.asBareJID());
            }
        }
    }
}