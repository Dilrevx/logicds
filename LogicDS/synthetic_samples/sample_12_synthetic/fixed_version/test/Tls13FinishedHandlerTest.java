import commons.*;
import main.Tls13FinishedHandler;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Tls13FinishedHandlerTest {
    private WOLFSSL ssl;
    private byte[] input;
    private int[] idx;
    private int size;
    private int totalSz;

    @Before
    public void setUp() {
        ssl = new WOLFSSL();
        ssl.keys.padSz = 0;
        ssl.clientSecret = new byte[16];
        ssl.serverSecret = new byte[16];
        ssl.keys.client_write_MAC_secret = new byte[32];
        ssl.keys.server_write_MAC_secret = new byte[32];
        ssl.options = new WOLFSSL.Options();
        ssl.options.handShakeDone = false;
        ssl.options.side = Constants.WOLFSSL_SERVER_END;
        ssl.options.mutualAuth = true;
        ssl.options.failNoCert = false;
        ssl.options.havePeerVerify = false;
        ssl.options.resuming = false;

        size = 0;
        totalSz = 0;
        idx = new int[]{ 0 };
        input = new byte[0];
    }

    @Test
    public void testFailsWhenClientNotVerified() {
        int ret = Tls13FinishedHandler
                .DoTls13Finished(ssl, input, idx.clone(), size, totalSz, Constants.NO_SNIFF);
        assertEquals(
                "Fixed should abort with NO_PEER_CERT when client not verified",
                Constants.NO_PEER_CERT,
                ret
        );
    }

    @Test
    public void testSucceedsWhenClientVerified() {
        ssl.options.havePeerVerify = true;

        int ret = Tls13FinishedHandler
                .DoTls13Finished(ssl, input, idx.clone(), size, totalSz, Constants.NO_SNIFF);
        assertEquals(
                "Fixed should succeed (return 0) when client verified",
                0,
                ret
        );
    }
}
