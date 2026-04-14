import SD1_fixed.src.commons.Callback1;
import SD1_fixed.src.commons.Pjmedia_tp_cb_param;
import SD1_fixed.src.commons.SrtpCrypto;
import SD1_fixed.src.commons.TransportSrtp;
import SD1_fixed.src.SrtpTransportCallback;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SrtpTransportCallbackTest {
    private TransportSrtp srtp;
    private Pjmedia_tp_cb_param param;
    private boolean[] invoked;

    @Before
    public void setUp() {
        SrtpTransportCallback.unprotectCallCount = 0;

        invoked = new boolean[]{false};

        srtp = new TransportSrtp();
        srtp.bypass_srtp   = false;
        srtp.tx_policy     = new SrtpCrypto();
        srtp.rx_policy     = new SrtpCrypto();
        srtp.probation_cnt = 1;
        srtp.user_data     = srtp;
        srtp.rtp_cb = new Callback1() {
            @Override
            public void rtp_cb(Object ud, Object pkt, int size) {
                invoked[0] = true;
            }
        };

        param = new Pjmedia_tp_cb_param();
        param.user_data = srtp;
        param.pkt       = new Object();
        param.size      = 10;
    }

    @Test
    public void testCallbackAfterRestart() {
        SrtpTransportCallback.srtp_rtp_cb(param);
        assertTrue("Expected RTP callback to be invoked after SRTP restart", invoked[0]);
    }
}
