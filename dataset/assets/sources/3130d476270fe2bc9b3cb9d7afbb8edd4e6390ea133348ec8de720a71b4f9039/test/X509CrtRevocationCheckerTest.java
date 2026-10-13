import commons.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.X509CrtRevocationChecker;

public class X509CrtRevocationCheckerTest {

    private MbedtlsX509Crt makeCrt(byte[] serial) {
        MbedtlsX509Crt crt = new MbedtlsX509Crt();
        crt.serial.p = serial;
        crt.serial.len = serial.length;
        return crt;
    }

    private MbedtlsX509Crl makeCrlEntry(byte[] serial) {
        MbedtlsX509CrlEntry entry = new MbedtlsX509CrlEntry();
        entry.serial.p = serial;
        entry.serial.len = serial.length;
        entry.revocationDate = new MbedtlsX509Time();
        entry.next = null;
        MbedtlsX509Crl crl = new MbedtlsX509Crl();
        crl.entry = entry;
        return crl;
    }

    @Before
    public void resetHelper() {
        Helper.testTimeIsPast = true;
    }

    @Test
    public void testRevocationDateInPast() {
        byte[] serial = new byte[]{ 0x01, 0x02, 0x03 };
        MbedtlsX509Crt crt = makeCrt(serial);
        MbedtlsX509Crl crl = makeCrlEntry(serial);

        int fixed = X509CrtRevocationChecker
                .mbedtls_x509_crt_is_revoked(crt, crl);

        assertEquals("Fixed should revoke when date is past", 1, fixed);
    }

    @Test
    public void testBypassWhenDateInFuture_BuggyOnly() {
        byte[] serial = new byte[]{ 0x0A, 0x0B, 0x0C };
        MbedtlsX509Crt crt = makeCrt(serial);
        MbedtlsX509Crl crl = makeCrlEntry(serial);

        Helper.testTimeIsPast = false;


        int fixed = X509CrtRevocationChecker
                .mbedtls_x509_crt_is_revoked(crt, crl);

        assertEquals("Fixed should not bypass revocation when date is future", 1, fixed);
    }
}
