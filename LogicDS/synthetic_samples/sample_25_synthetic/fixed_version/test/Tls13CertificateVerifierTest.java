import org.junit.Test;
import static org.junit.Assert.*;
import commons.Commons;
import main.Tls13CertificateVerifier;
import commons.Buffer;
import commons.TlsContext;

public class Tls13CertificateVerifierTest {

    @Test
    public void testMissingEd25519KeyCausesError() {
        TlsContext ssl = new TlsContext();
        ssl.peerEd25519KeyPresent = false;
        ssl.peerEd448KeyPresent    = true;
        ssl.peerEccDsaKeyPresent   = true;
        ssl.peerRsaKeyPresent      = true;
        ssl.keys.padSz             = 0;
        ssl.buffers.sig            = new Buffer();

        byte[] input = new byte[] {
                (byte)Commons.ed25519_sa_algo,
                0,
                0,
                0, 1,
                0x42
        };
        int[] inOutIdx = new int[] { 0 };

        int ret = Tls13CertificateVerifier.DoTls13CertificateVerify(
                ssl, input, inOutIdx, input.length
        );

        assertEquals("Expected SIG_VERIFY_E when ED25519 sig without cert",
                Commons.SIG_VERIFY_E, ret);
    }
}
