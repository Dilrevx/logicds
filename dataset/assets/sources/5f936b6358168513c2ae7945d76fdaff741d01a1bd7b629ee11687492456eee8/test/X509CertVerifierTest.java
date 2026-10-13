import commons.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

import main.X509CertVerifier;

public class X509CertVerifierTest {
    private X509_STORE_CTX ctx;
    private X509 c1, c2, c3;

    @Before
    public void setUp() {
        c1 = new X509();
        c2 = new X509();
        c3 = new X509();

        ctx = new X509_STORE_CTX();
        ctx.cert = c1;
        ctx.chain = new ArrayList<X509>(Arrays.asList(c1, c2, c3));
        ctx.untrusted = null;
        ctx.last_untrusted = 100;
        ctx.param = new X509_VERIFY_PARAM();
        ctx.param.depth = 10;
        ctx.param.flags = 0;
        ctx.verify_cb = new VerifyCallback() {
            @Override
            public int call(int ok, X509_STORE_CTX c) {
                return ok;
            }
        };

        Helper.checkTrustReturn = 0;
    }

    @Test
    public void testLastUntrustedRecomputed() {
        int result = X509CertVerifier.X509_verify_cert(ctx);

        assertEquals(
                "After trimming, last_untrusted should equal remaining chain size",
                ctx.chain.size(),
                ctx.last_untrusted
        );
    }
}
