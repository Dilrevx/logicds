import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PamAuthTest {

    private PamAuth auth;
    private PamTransaction stub;

    @Before
    public void setUp() {
        PamTransaction.resetMocks();
        auth = new PamAuth();
        stub = new PamTransaction();
        PamTransaction.preloadedTransaction = stub;
    }

    @Test
    public void testValidCredentialsReturnUserName() {
        stub.authenticateError = null;
        stub.acctMgmtError = null;
        stub.itemUser = "alice";

        AuthResult result = auth.auth("login", "alice", "s3cret");

        assertNull("enabled account + valid credentials: no error", result.getErr());
        assertEquals("returned user matches PAM item", "alice", result.getUser());
    }

    @Test
    public void testDisabledAccountRejected() {
        stub.authenticateError = null;
        stub.acctMgmtError = new PamError("account expired");
        stub.itemUser = "alice";

        AuthResult result = auth.auth("login", "alice", "s3cret");

        assertNotNull("disabled account must produce an error", result.getErr());
        assertEquals("disabled account must not leak user name", "", result.getUser());
    }
}
