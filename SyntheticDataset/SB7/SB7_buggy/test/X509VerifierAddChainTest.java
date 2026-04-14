import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.X509Verifier;
import commons.X509;
import commons.X509VerifyCtx;
import commons.X509VerifyChain;
import commons.X509Constants;
import commons.X509Utils;

import java.util.Arrays;

public class X509VerifierAddChainTest {
    private X509VerifyCtx    ctx;
    private X509VerifyChain  chain;
    private final String     name = "example.com";

    @Test
    public void testLeafErrorIsRecorded() {
        X509Utils.isTest2 = false;
        ctx = new X509VerifyCtx();
        ctx.max_chains   = 1;
        ctx.chains       = new X509VerifyChain[1];
        ctx.chains_count = 0;
        ctx.error        = -1;
        ctx.error_depth  = -1;

        chain = new X509VerifyChain();
        chain.certs       = Arrays.asList(new X509());
        chain.cert_errors = new int[] { 99 };

        int rv = X509Verifier.x509_verify_ctx_add_chain(ctx, chain, name);
        assertEquals("Should return success", 1, rv);
        assertEquals(
                "cert_errors[0] should be overwritten with ctx.error (0)",
                0, chain.cert_errors[0]
        );
        assertEquals("chains_count should advance", 1, ctx.chains_count);
        assertEquals("ctx.error should be reset to OK", X509Constants.X509_V_OK, ctx.error);
        assertEquals("ctx.error_depth should be set to leaf depth (0)", 0, ctx.error_depth);
    }

    @Test
    public void test2() {
        X509Utils.isTest2 = true; // Simulate test2 condition
        ctx = new X509VerifyCtx();
        ctx.max_chains   = 2;
        ctx.chains       = new X509VerifyChain[1];
        ctx.chains_count = 5;
        ctx.error        = -1;
        ctx.error_depth  = -1;

        chain = new X509VerifyChain();
        chain.certs       = Arrays.asList(new X509());
        chain.cert_errors = new int[] { 99 };

        int rv = X509Verifier.x509_verify_ctx_add_chain(ctx, chain, name);
        assertEquals("Should return -1", -1, rv);
    }

}
