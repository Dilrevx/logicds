import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MandrilPluginTest {

    private MandrilPlugin plugin;
    private Plugin p;
    private ClientSession cs;
    private SessionRequest sr;

    @Before
    public void setUp() {
        MkSecurity.resetRules();
        MkSecurity.blockedPrefixes.add("/admin/");

        plugin = new MandrilPlugin(new MkApi());
        p = new Plugin();
        cs = new ClientSession();
        cs.setSocket(42);
        sr = new SessionRequest();
    }

    @Test
    public void testPublicPathAllowed() {
        sr.setUri(new MkPtr("/public/index.html"));
        sr.setUriProcessed(new MkPtr("/public/index.html"));

        int result = plugin.mkpStage30(p, cs, sr);

        assertEquals("public path should pass through",
                PluginConst.MK_PLUGIN_RET_NOT_ME, result);
        assertEquals("no forbidden status set", 0, sr.getHttpStatus());
    }

    @Test
    public void testEncodedSlashBypassBlocked() {
        sr.setUri(new MkPtr("/admin%2Fsecret.html"));
        sr.setUriProcessed(new MkPtr("/admin/secret.html"));

        int result = plugin.mkpStage30(p, cs, sr);

        assertEquals("encoded-slash path that decodes to /admin/ must be blocked",
                PluginConst.MK_PLUGIN_RET_CLOSE_CONX, result);
        assertEquals("blocked request must set 403",
                PluginConst.MK_CLIENT_FORBIDDEN, sr.getHttpStatus());
    }
}
