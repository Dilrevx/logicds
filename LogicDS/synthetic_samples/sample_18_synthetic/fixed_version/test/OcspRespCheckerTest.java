import commons.Helper;
import commons.ErrorCodes;
import org.junit.Before;
import org.junit.Test;

import static main.OcspRespChecker.gnutls_ocsp_resp_check_crt;
import static org.junit.Assert.*;


public class OcspRespCheckerTest {
    @Before
    public void setUp() {
        Helper.setRdnHash(new byte[]{9,9,9}, 3);
        Helper.setRserial(new byte[]{1,2});
        Helper.setCserial(new byte[]{1,2,3});
    }

    @Test
    public void testFixedRejectsSerialLengthMismatch() {
        int retFixed = gnutls_ocsp_resp_check_crt(null, 0, null);
        assertEquals(
                "Fixed should reject mismatched serial length",
                ErrorCodes.GNUTLS_E_OCSP_RESPONSE_ERROR,
                retFixed
        );
    }
}
