import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AuthHandlerTest {

    private AuthHandler handler;
    private NgxRequest r;
    private AuthConfig alcf;

    @Before
    public void setUp() {
        AuthSpnego.resetMocks();
        handler = new AuthHandler();
        r = new NgxRequest();
        alcf = new AuthConfig();
        alcf.setProtect(1);
        alcf.setAllowBasic(true);
        alcf.getAuthorizedPrincipals().add("alice");
    }

    @Test
    public void testBasicAuthSuccessForAuthorizedUser() {
        AuthSpnego.resolvedUser = "alice";
        AuthSpnego.basicUserResult = NgxConst.NGX_OK;
        AuthSpnego.spnegoBasicResult = NgxConst.NGX_OK;

        int ret = handler.ngxHttpAuthSpnegoHandler(r, alcf, null);

        assertEquals("authorized basic auth should return NGX_OK", NgxConst.NGX_OK, ret);
    }

    @Test
    public void testMalformedBasicAuthRejected() {
        AuthSpnego.resolvedUser = "alice";
        AuthSpnego.basicUserResult = NgxConst.NGX_OK;
        AuthSpnego.spnegoBasicResult = NgxConst.NGX_ERROR;

        int ret = handler.ngxHttpAuthSpnegoHandler(r, alcf, null);

        assertEquals("malformed basic auth (NGX_ERROR) must be rejected with 401",
                NgxConst.NGX_HTTP_UNAUTHORIZED, ret);
    }
}
