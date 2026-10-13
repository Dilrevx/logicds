import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.Tls13ServerHello;
import commons.*;

import java.util.ArrayList;

public class Tls13ServerHelloTest {

    private TlsContext ssl;
    private byte[] input;
    private int[] inOutIdx;
    private int helloSz;
    private byte[] extMsgType;

    @Before
    public void setUp() {
        ssl        = new TlsContext();
        ssl.extensions = new ArrayList<TlsExtension>();
        input      = new byte[CommonsConstants.OPAQUE16_LEN];
        inOutIdx   = new int[] { 0 };
        helloSz    = CommonsConstants.OPAQUE16_LEN;
        extMsgType = new byte[] { CommonsConstants.server_hello };
    }

    @Test
    public void testMissingPskAndKeyShareFails() {
        int ret = Tls13ServerHello.DoTls13ServerHello(
                ssl, input, inOutIdx, helloSz, extMsgType
        );
        assertEquals(
                "Should return EXT_MISSING when no PSK and no key share",
                CommonsConstants.EXT_MISSING, ret
        );
    }

    @Test
    public void testWithPskSucceeds() {
        PreSharedKey psk = new PreSharedKey();
        psk.chosen = true;
        TlsExtension ext = new TlsExtension();
        ext.data      = psk;
        ssl.extensions.add(ext);

        int ret = Tls13ServerHello.DoTls13ServerHello(
                ssl, input, inOutIdx, helloSz, extMsgType
        );
        assertEquals("Should succeed when PSK present", 0, ret);
        assertTrue("Encryption should be on", ssl.keys.encryptionOn);
        assertEquals(
                "State should be SERVER_HELLO_COMPLETE",
                CommonsConstants.SERVER_HELLO_COMPLETE,
                ssl.options.serverState
        );
    }
}
