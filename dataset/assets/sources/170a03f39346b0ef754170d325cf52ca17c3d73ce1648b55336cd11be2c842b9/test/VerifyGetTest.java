import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class VerifyGetTest {

    private VerifyGet handler;
    private AutheliaCtx ctx;

    @Before
    public void setUp() {
        AuthenticationBackendConfiguration cfg = new AuthenticationBackendConfiguration();
        cfg.setRefreshProfile(true);
        cfg.setRefreshProfileIntervalMillis(60_000L);
        handler = new VerifyGet(cfg);

        ctx = new AutheliaCtx();
        ctx.getConfiguration().setSessionDomain("example.com");

        VerifyAuthResult va = new VerifyAuthResult();
        va.isBasicAuth = false;
        va.username = "alice";
        va.name = "Alice";
        va.authLevel = 2;
        va.err = null;
        ctx.setPreloadedVerifyAuth(va);
    }

    @Test
    public void testValidSecureUrlAuthorized() {
        ctx.setOriginalUrlRaw("https://app.example.com/dashboard");

        handler.handle(ctx);

        assertEquals("authorized request should not set 401",
                Response.STATUS_OK, ctx.getResponse().getStatus());
        assertEquals("Remote-User header should be set on authorized flow",
                "alice", ctx.getResponse().getHeader().get("Remote-User"));
    }

    @Test
    public void testMalformedUrlReturnsUnauthorized() {
        ctx.setOriginalUrlRaw("malformed-no-scheme");

        handler.handle(ctx);

        assertEquals("malformed URL must produce 401, not 500",
                Response.STATUS_UNAUTHORIZED, ctx.getResponse().getStatus());
    }
}
