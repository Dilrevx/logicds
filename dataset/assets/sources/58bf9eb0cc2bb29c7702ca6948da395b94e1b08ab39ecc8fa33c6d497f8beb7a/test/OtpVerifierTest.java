import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class OtpVerifierTest {

    private OtpVerifier verifier;
    private KrbEncTktPart encTktReply;
    private KrbPaData pa;
    private KrbKdcReq request;
    private KdcPreauthCallbacks cb;
    private int[] capturedRetval;
    private RespondFn respond;

    @Before
    public void setUp() {
        OtpUtil.resetMocks();

        verifier = new OtpVerifier();
        encTktReply = new KrbEncTktPart();

        pa = new KrbPaData(new byte[]{1, 2, 3, 4}, 4);

        request = new KrbKdcReq();
        request.setClient(new Object());

        cb = new KdcPreauthCallbacks();
        cb.setPreloadedArmorKey(new Object());
        cb.setPreloadedConfig("otp-config");
        cb.setPreloadedGetStringRet(0);
        cb.setPreloadedEventContext(new Object());

        capturedRetval = new int[]{0};
        respond = new RespondFn() {
            public void respond(Object arg, int retval, Object a, Object b, Object c) {
                capturedRetval[0] = retval;
            }
        };
    }

    @Test
    public void testArmorKeyMissingDoesNotMarkPreauth() {
        cb.setPreloadedArmorKey(null);

        verifier.otpVerify(new Object(), null, request, encTktReply, pa,
                cb, new Object(), new Object(), respond, new Object());

        assertEquals("error respond invoked with PREAUTH_FAILED",
                KrbConst.KRB5KDC_ERR_PREAUTH_FAILED, capturedRetval[0]);
        assertEquals("pre-auth flag must not be set on failure",
                0, encTktReply.getFlags() & KrbConst.TKT_FLG_PRE_AUTH);
    }

    @Test
    public void testInvalidOtpRequestDoesNotMarkPreauth() {
        OtpUtil.decodeKrb5PaOtpReqRet = 1;

        verifier.otpVerify(new Object(), null, request, encTktReply, pa,
                cb, new Object(), new Object(), respond, new Object());

        assertEquals("pre-auth flag must not be set on decode failure",
                0, encTktReply.getFlags() & KrbConst.TKT_FLG_PRE_AUTH);
    }
}
