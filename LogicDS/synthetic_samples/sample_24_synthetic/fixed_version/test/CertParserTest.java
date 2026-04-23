import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.CertParser;
import commons.*;

public class CertParserTest {
    private DecodedCert cert;
    private final Object cm = new Object();

    @Before
    public void setUp() {
        cert = new DecodedCert();
        cert.sigCtx        = new SigCtx();
        cert.sigCtx.state  = Commons.SIG_STATE_BEGIN;
        cert.srcIdx        = 10;
        cert.sigIndex      = 20;
        cert.version       = 3;
        cert.ocspNoCheckSet = true;
        cert.source        = new byte[100];
        cert.signatureOID  = 0x1234;
    }

    @Test
    public void testNoCheckGoesFullVerify() {
        cert.ocspNoCheckSet = true;
        int code = CertParser.ParseCertRelative(
                cert,
                Commons.CERT_TYPE,
                Commons.VERIFY_OCSP_CERT,
                cm
        );
        assertEquals(
                "With ocspNoCheckSet=true and VERIFY_OCSP_CERT we should do a full verify",
                Commons.FULL_VERIFY,
                code
        );
    }

    @Test
    public void testCheckGoesOcspOnly() {
        cert.ocspNoCheckSet = false;
        int code = CertParser.ParseCertRelative(
                cert,
                Commons.CERT_TYPE,
                Commons.VERIFY_OCSP_CERT,
                cm
        );
        assertEquals(
                "With ocspNoCheckSet=false and VERIFY_OCSP_CERT we should do OCSP only",
                Commons.OCSP_ONLY,
                code
        );
    }
}
