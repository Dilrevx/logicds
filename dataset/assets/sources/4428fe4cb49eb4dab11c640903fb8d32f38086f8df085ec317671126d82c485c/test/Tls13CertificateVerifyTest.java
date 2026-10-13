import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.Tls13CertificateVerify;
import commons.*;

public class Tls13CertificateVerifyTest {
    private TlsContext ssl;
    private byte[] input;
    private int[] inOutIdx;
    private int totalSz;

    @Before
    public void setUp() {
        ssl = new TlsContext();
        input = new byte[] {0, 0, 0, 0};
        inOutIdx = new int[] {0};
        totalSz = input.length;
    }

    @Test
    public void testUnknownSigAlgoIsRejected() {
        int ret = Tls13CertificateVerify.DoTls13CertificateVerify(
                ssl, input, inOutIdx, totalSz
        );
        assertEquals(
                "Unknown signature algorithm should be rejected",
                CommonsConstants.SIG_VERIFY_E,
                ret
        );
    }

    @Test
    public void testEd25519SigAlgoIsAccepted() {
        ssl.peerEd25519KeyPresent = true;
        input[0] = CommonsConstants.ed25519_sa_algo;

        int ret = Tls13CertificateVerify.DoTls13CertificateVerify(
                ssl, input, inOutIdx, totalSz
        );
        assertEquals(
                "ED25519 signature algorithm should be accepted when key present",
                0,
                ret
        );
    }
}
