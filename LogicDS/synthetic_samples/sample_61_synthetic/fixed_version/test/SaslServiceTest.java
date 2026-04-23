import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SaslServiceTest {

    private SaslService service;
    private User u;
    private SaslSession p;
    private MyUser admin;

    @Before
    public void setUp() {
        UserRegistry.reset();
        IrcUtil.reset();

        service = new SaslService();

        admin = new MyUser();
        admin.setName("admin");
        UserRegistry.put("admin-uid", admin);

        u = new User();
        u.setNick("client-nick");

        p = new SaslSession();
        p.getMechptr().setName("PLAIN");
    }

    @Test
    public void testCompleteHandshakeLogsIn() {
        p.setAuthzeid("admin-uid");
        p.setPendingeid("admin-uid");
        p.setAuthzid("admin");

        boolean ok = service.saslHandleLogin(p, u, null);

        assertTrue("complete handshake should succeed", ok);
        assertSame("user should be logged in as admin", admin, u.getMyuser());
    }

    @Test
    public void testAbortedHandshakeDoesNotLogIn() {
        p.setAuthzeid("admin-uid");
        p.setPendingeid("");
        p.setAuthzid("admin");

        boolean ok = service.saslHandleLogin(p, u, null);

        assertFalse("aborted handshake (pendingeid empty) must not log the user in", ok);
        assertNull("no account must be attached to the user", u.getMyuser());
    }
}
