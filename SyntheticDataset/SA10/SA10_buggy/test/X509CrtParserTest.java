import commons.*;
import main.X509CrtParser;

import org.junit.*;
import static org.junit.Assert.*;

public class X509CrtParserTest {
    private static final int SIG_MISMATCH = Constants.MBEDTLS_ERR_X509_SIG_MISMATCH;

    @Before
    public void reset() {
        Helper.x509GetAlgCount = 0;
        Helper.testMode = 0;
    }

    @Test
    public void testMatchingParams() {
        byte[] dummy = new byte[1];
        MbedtlsX509Crt crt = new MbedtlsX509Crt();

        int retFixed  = X509CrtParser.x509_crt_parse_der_core(crt, dummy, dummy.length, 0, null, null);

        assertEquals("Fixed should accept when tags match", 0, retFixed);
    }

    @Test
    public void testNullVsNoParamsDetected() {
        byte[] dummy = new byte[1];
        MbedtlsX509Crt crt1 = new MbedtlsX509Crt();

        Helper.testMode = 2;

        MbedtlsX509Crt crt2 = new MbedtlsX509Crt();
        int retFixed = X509CrtParser.x509_crt_parse_der_core(crt2, dummy, dummy.length, 0, null, null);
        assertEquals("Fixed parser must reject mismatched tags",
                SIG_MISMATCH, retFixed);
    }
}
