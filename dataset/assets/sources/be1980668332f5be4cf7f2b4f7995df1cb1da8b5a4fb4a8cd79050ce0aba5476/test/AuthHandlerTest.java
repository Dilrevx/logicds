import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.AuthHandler;
import commons.CliOptions;
import commons.Session;
import commons.ClientSession;

public class AuthHandlerTest {

    @Before
    public void setUp() {
        AuthHandler.ses       = new Session();
        AuthHandler.cli_ses   = new ClientSession();
        AuthHandler.cli_opts  = new CliOptions();
        AuthHandler.cli_ses.is_trivial_auth = true;
    }

    @Test
    public void testFixedRejectsTrivialAuthWhenDisabled() {
        AuthHandler.cli_opts.disable_trivial_auth = true;
        AuthHandler.cli_ses.is_trivial_auth       = true;
        try {
            AuthHandler.recv_msg_userauth_success();
            fail("Expected IllegalStateException for trivial auth");
        } catch (IllegalStateException e) {
            assertEquals("trivial authentication not allowed", e.getMessage());
        }
    }

    @Test
    public void testFixedAllowsWhenNotDisabledOrNotTrivial() {
        AuthHandler.cli_opts.disable_trivial_auth = false;
        AuthHandler.cli_ses.is_trivial_auth       = true;
        AuthHandler.recv_msg_userauth_success();
        assertTrue("authdone should be set", AuthHandler.ses.authstate.authdone);
        assertEquals("state should be USERAUTH_SUCCESS_RCVD",
                ClientSession.USERAUTH_SUCCESS_RCVD, AuthHandler.cli_ses.state);
        assertEquals("lastauthtype should be AUTH_TYPE_NONE",
                ClientSession.AUTH_TYPE_NONE, AuthHandler.cli_ses.lastauthtype);

        AuthHandler.ses.authstate.authdone = false;
        AuthHandler.cli_ses.state           = 0;
        AuthHandler.cli_ses.lastauthtype    = -1;

        AuthHandler.cli_opts.disable_trivial_auth = true;
        AuthHandler.cli_ses.is_trivial_auth       = false;
        AuthHandler.recv_msg_userauth_success();
        assertTrue("authdone should be set", AuthHandler.ses.authstate.authdone);
        assertEquals("state should be USERAUTH_SUCCESS_RCVD",
                ClientSession.USERAUTH_SUCCESS_RCVD, AuthHandler.cli_ses.state);
        assertEquals("lastauthtype should be AUTH_TYPE_NONE",
                ClientSession.AUTH_TYPE_NONE, AuthHandler.cli_ses.lastauthtype);
    }
}
