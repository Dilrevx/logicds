import org.junit.Test;
import static org.junit.Assert.*;

import main.X509Verifier;
import commons.Buf;
import commons.MbedtlsConstants;
import commons.MbedtlsX509Crt;
import commons.MbedtlsX509Sequence;

public class X509VerifierTest {

    @Test
    public void testSanIpNotTreatedAsDns() {
        MbedtlsX509Crt crt = new MbedtlsX509Crt();
        crt.extTypes = MbedtlsConstants.MBEDTLS_X509_EXT_SUBJECT_ALT_NAME;
        Buf buf = new Buf();
        String ip = "1.2.3.4";
        buf.data = ip.getBytes();
        buf.length = buf.data.length;
        MbedtlsX509Sequence seq = new MbedtlsX509Sequence();
        seq.buf = buf;
        seq.next = null;
        crt.subjectAltNames = seq;
        int[] flags = new int[1];
        flags[0] = 0;

        X509Verifier.x509_crt_verify_name(crt, ip, flags);

        assertTrue(
                "Fixed version should reject an IP SAN when matching a DNS name",
                (flags[0] & MbedtlsConstants.MBEDTLS_X509_BADCERT_CN_MISMATCH) != 0
        );
    }

    @Test
    public void test2() {
        MbedtlsX509Crt crt = new MbedtlsX509Crt();
        crt.extTypes = MbedtlsConstants.MBEDTLS_X509_EXT_SUBJECT_ALT_NAME;
        Buf buf = new Buf();
        String ip = "1.2.3.4";
        buf.data = new byte[ip.length()];
        buf.data[0] = (byte) 0x02;
        ip = new String(buf.data, 0 , buf.data.length);
        buf.length = buf.data.length;
        MbedtlsX509Sequence seq = new MbedtlsX509Sequence();
        seq.buf = buf;
        seq.next = null;
        crt.subjectAltNames = seq;
        int[] flags = new int[1];
        flags[0] = 0;

        X509Verifier.x509_crt_verify_name(crt, ip, flags);

        assertTrue(
                "Fixed version should break",
                (flags[0] & MbedtlsConstants.MBEDTLS_X509_BADCERT_CN_MISMATCH) == 0
        );
    }
}
