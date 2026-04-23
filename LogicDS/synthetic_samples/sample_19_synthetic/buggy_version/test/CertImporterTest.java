
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.CertImporter;
import commons.GnutlsDatum;
import commons.GnutlsX509Crt;
import commons.GnutlsConstants;
import commons.GnutlsErrors;
import commons.GnutlsUtils;

public class CertImporterTest {

    @Before
    public void setUp() {
        GnutlsUtils.testSigAlgoInner = 1;
        GnutlsUtils.testSigAlgoOuter = 2;
    }

    @Test
    public void testImportFailsOnSignatureAlgorithmMismatch() {
        GnutlsX509Crt cert = new GnutlsX509Crt();
        cert.der.data = new byte[] { 0x01 };
        cert.der.size = 1;
        cert.cert = new Object();
        cert.expanded = true;

        GnutlsDatum data = new GnutlsDatum();
        data.data = new byte[] { 0x01 };
        data.size = 1;

        int result = CertImporter.gnutls_x509_crt_import(
                cert,
                data,
                GnutlsConstants.GNUTLS_X509_FMT_PEM
        );

        assertEquals(
                "Mismatched signatureAlgorithm OIDs must cause certificate import to fail",
                GnutlsErrors.GNUTLS_E_X509_CERTIFICATE_ERROR,
                result
        );
    }
}
