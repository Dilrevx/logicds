import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commons.*;

public class AdminManagerPrivilegeEscalationTest {

    private final String ADMIN_USERNAME = "testadmin";
    private final String OTHER_USERNAME = "otheruser";
    private JID adminJID;

    @Before
    public void setUp() {
        AdminManager.resetForTest();

        adminJID = XMPPServer.getInstance().createJID(ADMIN_USERNAME, null);
    }

    @After
    public void tearDown() {
        AdminManager.resetForTest();
    }

    @Test
    public void testAdminPrivilegeEscalationOnUserRecreation() {
        AdminManager adminManager = AdminManager.getInstance();

        adminManager.addAdminAccount(adminJID);
        assertTrue("Initial setup: testadmin should be an admin", adminManager.isUserAdmin(adminJID, false));

        UserManager.deleteUser(ADMIN_USERNAME);

        adminManager.refreshAdminAccounts();
        boolean isStillAdminAfterRecreation = adminManager.isUserAdmin(ADMIN_USERNAME, false);
        assertFalse("User '" + ADMIN_USERNAME + "' should NOT be an admin after deletion and re-creation, but is.", isStillAdminAfterRecreation);
    }
}