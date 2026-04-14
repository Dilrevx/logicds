import commons.*;
import org.junit.*;

import static org.junit.Assert.*;
import main.OCSPVerifier;

public class OCSPVerifierTest {

    private X509 signer;
    private X509Store store;
    private X509Stack untrusted;
    private X509Stack[] chain;
    private long flags;
    private int response;

    @Before
    public void setUp() {
        X509StoreContext.simulateSuccess = false;

        signer    = new X509();
        store     = new X509Store();
        untrusted = new X509Stack();
        chain     = new X509Stack[1];
        flags     = OCSPFlags.OCSP_PARTIAL_CHAIN;
        response  = 1;
    }

    @After
    public void tearDown() {
        X509StoreContext.simulateSuccess = false;
    }

    @Test
    public void testVerificationFailureDetected() {
        int result = OCSPVerifier.ocsp_verify_signer(
                signer, response, store, flags, untrusted, chain
        );
        assertTrue(
                "Expected non-positive return on verification failure, got: " + result,
                result <= 0
        );
    }

    @Test
    public void testVerificationSuccessDetected() {
        X509StoreContext.simulateSuccess = true;

        int result = OCSPVerifier.ocsp_verify_signer(
                signer, response, store, flags, untrusted, chain
        );
        assertTrue(
                "Expected positive return on simulated success, got: " + result,
                result > 0
        );
        assertTrue(
                "Error expected to be captured",
                X509StoreContext.errorCaptured
        );
    }
}
