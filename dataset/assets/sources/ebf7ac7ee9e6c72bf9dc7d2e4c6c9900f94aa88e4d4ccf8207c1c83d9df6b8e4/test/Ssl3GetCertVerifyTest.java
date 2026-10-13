import org.junit.Before;
import org.junit.Test;

import static main.Ssl3GetCertVerify.ssl3_get_cert_verify;
import static org.junit.Assert.*;

import commons.*;

public class Ssl3GetCertVerifyTest {
    private SSL ssl;

    public static class DhX509 extends X509 {
        public DhX509() {
            super();
            this.cert_type = Constants.EVP_PKT_DH;
        }
    }

    @Test
    public void testFixedRejectsDhCertWithoutVerify() {
        ssl = new SSL();
        ssl.init_msg = new byte[0];
        ssl.s3.tmp.message_type = 0;
        ssl.s3.change_cipher_spec = false;
        ssl.session.peer = new DhX509();
        int retFixed = ssl3_get_cert_verify(ssl);
        assertEquals(
                "Fixed correctly rejects ANY cert without CertificateVerify",
                0,
                retFixed
        );
    }

    @Test
    public void testPeerNull() {
        ssl = new SSL();
        ssl.init_msg = new byte[0];
        ssl.s3.tmp.message_type = 0;
        ssl.s3.change_cipher_spec = false;
        ssl.session.peer = null;
        int retFixed = ssl3_get_cert_verify(ssl);
        assertEquals(
                "Should Return 1 when peer is null",
                1,
                retFixed
        );
    }
}
