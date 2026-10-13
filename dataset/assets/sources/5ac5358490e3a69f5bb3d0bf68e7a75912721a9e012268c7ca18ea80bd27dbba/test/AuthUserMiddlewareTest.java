import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AuthUserMiddlewareTest {

    private AuthService authService;
    private AuthUserMiddleware middleware;
    private GinContext ctx;

    @Before
    public void setUp() {
        authService = new AuthService();
        middleware = new AuthUserMiddleware(authService);
        ctx = new GinContext();
        ctx.setToken("admin-token");
    }

    @Test
    public void testActiveAdminPassesThrough() {
        UserInfo ui = new UserInfo();
        ui.setUserId("u1");
        ui.setUserStatus(UserStatus.ACTIVE);
        authService.setPreloadedUserInfo(ui);
        authService.setPreloadedErr(null);

        middleware.adminAuth(ctx);

        assertFalse("active admin should not be aborted", ctx.isAborted());
        assertTrue("next handler should be invoked", ctx.isNextCalled());
        assertSame("user info must be stashed on context",
                ui, ctx.get(AuthUserMiddleware.CTX_UUID_KEY));
    }

    @Test
    public void testNullUserInfoRejected() {
        authService.setPreloadedUserInfo(null);
        authService.setPreloadedErr(null);

        middleware.adminAuth(ctx);

        assertTrue("absent user info must abort the request", ctx.isAborted());
        assertFalse("next handler must not run when user info is null", ctx.isNextCalled());
    }
}
