import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class WebHandlerTest {

    private WebHandler webHandler;
    private WebConfig cfg;
    private int[] innerCalls;
    private InnerHandler inner;

    private static final String ADMIN_HASH = "$2y$10$REAL_ADMIN_HASH";

    @Before
    public void setUp() {
        Bcrypt.reset();
        ConfigLoader.reset();

        Bcrypt.registerHash(WebHandler.FAKE_HASH, "fakepassword");
        Bcrypt.registerHash(ADMIN_HASH, "admin-real-pw");

        cfg = new WebConfig();
        cfg.getUsers().put("admin", ADMIN_HASH);
        ConfigLoader.preloadedConfig = cfg;

        innerCalls = new int[]{0};
        inner = new InnerHandler() {
            public void serveHttp(HttpResponse w, HttpRequest r) {
                innerCalls[0]++;
                w.setStatus(HttpStatus.OK);
            }
        };

        webHandler = new WebHandler("/tmp/web.yml", inner);
    }

    @Test
    public void testValidUserCorrectPasswordAccepted() {
        HttpRequest r = new HttpRequest();
        r.setBasicAuth("admin", "admin-real-pw");
        HttpResponse w = new HttpResponse();

        webHandler.serveHttp(w, r);

        assertEquals("valid creds should succeed", HttpStatus.OK, w.getStatus());
        assertEquals("inner handler should run", 1, innerCalls[0]);
    }

    @Test
    public void testInvalidUserCacheNotPoisoned() {
        HttpRequest r = new HttpRequest();
        r.setBasicAuth("nobody", "fakepassword");
        HttpResponse w = new HttpResponse();

        webHandler.serveHttp(w, r);

        assertEquals("invalid user should be rejected", HttpStatus.UNAUTHORIZED, w.getStatus());

        String cacheKey = WebHandler.hexEncode(
                ("nobody" + WebHandler.FAKE_HASH + "fakepassword").getBytes());
        Boolean cached = webHandler.getCache().peek(cacheKey);
        assertNotNull("cache entry should exist", cached);
        assertFalse("cache must not store authOk=true for an invalid user, even when bcrypt matches the fake hash",
                cached.booleanValue());
    }
}
