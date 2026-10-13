import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ServerConfigTest {

    @Before
    public void setUp() {
        Env.reset();
        Config.reset();
    }

    @Test
    public void testEnvVarTakesPrecedence() {
        Env.setenv("DNS_KEY", "env-supplied-key");
        Config.Server.setDnsKey("cfg-supplied-key");

        String key = ServerConfig.getDnsKey();

        assertEquals("env var should win over config", "env-supplied-key", key);
    }

    @Test
    public void testNoHardcodedDefaultKey() {
        String key = ServerConfig.getDnsKey();

        assertEquals("absent env and config must yield empty key, never a hardcoded default",
                "", key);
    }
}
