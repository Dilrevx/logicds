import org.junit.Test;
import static org.junit.Assert.*;
import commons.*;
import main.TlsCrlVerifier;

public class TlsCrlVerifierTest {
    @Test
    public void testFixedDetectsRevocationByIssuerCrl() {
        int ok = 1;
        X509StoreCtx ctx = new X509StoreCtx();
        int result = TlsCrlVerifier.tls_verify_crl(ok, ctx);
        assertEquals("Fixed version should fail when issuer‐CRL revokes",
                Commons.FALSE, result);
    }

}
