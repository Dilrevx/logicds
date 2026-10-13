import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

    private Config parser;
    private MosquittoDb db;
    private MosquittoConfig config;

    @Before
    public void setUp() {
        parser = new Config();
        db = new MosquittoDb();
        config = new MosquittoConfig();
    }

    @Test
    public void testDefaultListenerPortPromoted() {
        config.getDefaultListener().setPort(1883);

        int ret = parser.configParseArgs(db, config, new String[]{});

        assertEquals("parseArgs should succeed", MosqErr.SUCCESS, ret);
        assertEquals("exactly one promoted listener", 1, config.getListenerCount());
        assertEquals("promoted listener inherits port", 1883, config.getListeners().get(0).getPort());
    }

    @Test
    public void testAclFileCopiedFromDefaultListener() {
        config.getDefaultListener().setPort(1883);
        config.getDefaultListener().getSecurityOptions().setAclFile("/etc/mosquitto/acl.conf");

        int ret = parser.configParseArgs(db, config, new String[]{});

        assertEquals("parseArgs should succeed", MosqErr.SUCCESS, ret);
        assertEquals("one promoted listener expected", 1, config.getListenerCount());
        assertEquals("promoted listener must inherit acl_file from default",
                "/etc/mosquitto/acl.conf",
                config.getListeners().get(0).getSecurityOptions().getAclFile());
    }
}
