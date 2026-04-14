import org.junit.Test;
import static org.junit.Assert.*;

import master.CheckChainExtensions;

import common.*;

public class CheckChainExtensionsTest {
    private X509StoreCtx createCtx(int caCheckResult,boolean explicitCurveParam, int ex_pathlen, int ex_flags) {
        X509StoreCtx ctx = new X509StoreCtx();
        ctx.param.flags = X509Flags.X509_V_FLAG_X509_STRICT;
        ctx.param.purpose = 1;

        X509 dummy = new X509(caCheckResult, false, 0, -1);
        ctx.chain.add(dummy);

        X509 testCert = new X509(caCheckResult, explicitCurveParam, ex_flags, ex_pathlen);
        ctx.chain.add(testCert);

        return ctx;
    }

    @Test
    public void testCase1_ActualCA_WithCurveParams() {
        Util.setVerifyCAResult(1);

        X509StoreCtx ctx = createCtx(1, true, 1, X509.EXFLAG_CA);

        int ret = CheckChainExtensions.checkChainExtensions(ctx);
        assertEquals("Test Case 1: Expected failure (0) when curve parameters present", 0, ret);
        assertEquals("Test Case 1: Expected error = EC_KEY_EXPLICIT_PARAMS",
                X509StoreCtx.X509_V_ERR_EC_KEY_EXPLICIT_PARAMS, ctx.error);
    }

    @Test
    public void testCase2_ActualCA_NoCurveParams() {
        Util.setVerifyCAResult(1);
        X509StoreCtx ctx = createCtx(1, false, 1, X509.EXFLAG_CA);

        int ret = CheckChainExtensions.checkChainExtensions(ctx);
        assertEquals("Test Case 2: Expected success (1) when no curve parameters", 1, ret);
        assertEquals("Test Case 2: Expected no error", X509StoreCtx.X509_V_OK, ctx.error);
    }


    @Test
    public void testCase3_NotActualCA_WithCurveParams() {
        Util.setVerifyCAResult(0);  // not actual CA

        X509StoreCtx ctx = createCtx(0, true, 1, X509.EXFLAG_CA);

        int ret = CheckChainExtensions.checkChainExtensions(ctx);
        assertEquals("Test Case 3: Expected failure (0) when not CA and curve parameters present", 0, ret);
    }

    @Test
    public void testCase4_NotActualCA_NoCurveParams() {
        Util.setVerifyCAResult(0);  // not actual CA

        X509StoreCtx ctx = createCtx(0, false, 1, X509.EXFLAG_CA);

        int ret = CheckChainExtensions.checkChainExtensions(ctx);
        assertEquals("Test Case 4: Expected failure (0) when not CA and curve parameters present", 0, ret);
    }
}
