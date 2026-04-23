import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class JwtMiddlewareTest {

    private JwtMiddleware mw;
    private EchoContext c;
    private Server server;
    private int[] nextCalls;
    private Handler next;

    @Before
    public void setUp() {
        JwtParser.mockScript.clear();

        mw = new JwtMiddleware();
        c = new EchoContext();
        c.setPath("/api/memo/123");
        c.setMethod("POST");
        c.setAccessTokenHeader("token-string");

        server = new Server();
        server.putUser(1, new Object());

        nextCalls = new int[]{0};
        next = new Handler() {
            public HttpError handle(EchoContext ctx) {
                nextCalls[0]++;
                return null;
            }
        };
    }

    @Test
    public void testValidTokenAccepted() {
        JwtParser.MockScript.Entry entry = new JwtParser.MockScript.Entry();
        entry.valid = true;
        entry.err = null;
        JwtParser.mockScript.push(entry);

        HttpError result = mw.handle(c, server, "secret", next);

        assertNull("valid token must pass middleware", result);
        assertEquals("next handler invoked exactly once", 1, nextCalls[0]);
    }

    @Test
    public void testInvalidSignatureRejected() {
        JwtParser.MockScript.Entry entry = new JwtParser.MockScript.Entry();
        entry.valid = false;
        entry.err = new ValidationError(ValidationError.VALIDATION_ERROR_SIGNATURE,
                "signature is invalid");
        JwtParser.mockScript.push(entry);

        HttpError result = mw.handle(c, server, "secret", next);

        assertNotNull("invalid-signature token must be rejected", result);
        assertEquals("rejection must use 401", JwtMiddleware.STATUS_UNAUTHORIZED, result.getCode());
        assertEquals("next handler must not run for invalid token", 0, nextCalls[0]);
    }
}
