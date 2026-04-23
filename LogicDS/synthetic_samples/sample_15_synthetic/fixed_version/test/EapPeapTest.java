import org.junit.Test;
import static org.junit.Assert.*;

import common.EapMethodRet;
import common.EapPeapData;
import common.EapSm;
import common.SslData;
import common.WPABuf;

import master.*;

public class EapPeapTest {

    @Test
    public void testAlwaysModeIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = false;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.ALWAYS;
        data.phase2EapStarted = true;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        // Dummy request buffer.
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test Always Mode Incomplete: Expected DECISION_FAIL", EapMethodRet.DECISION_FAIL, decision);
    }

    @Test
    public void testAlwaysModeComplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = false;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.ALWAYS;
        data.phase2EapStarted = true;
        data.phase2EapSuccess = true;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test Always Mode Complete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }

    @Test
    public void testForInitialNoResumeNoClientCertIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = false;
        sm.sslData.clientCertConf = false;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.FOR_INITIAL;
        data.phase2EapStarted = false;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test ForInitial Resume No Cert Incomplete: Expected DECISION_FAIL", EapMethodRet.DECISION_FAIL, decision);
    }

    @Test
    public void testForInitialNoResumeNoClientCertComplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = false;
        sm.sslData.clientCertConf = false;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.FOR_INITIAL;
        data.phase2EapStarted = false;
        data.phase2EapSuccess = true;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test ForInitial No Resume Cert Complete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }

    @Test
    public void testForInitialResumedNoCertIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = true;
        sm.sslData.clientCertConf = false;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.FOR_INITIAL;
        data.phase2EapStarted = false;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        // In FOR_INITIAL mode with session resumed, the check is relaxed.
        assertEquals("Test ForInitial Resumed No Cert Incomplete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }

    @Test
    public void testForInitialNoResumedCertIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = false;
        sm.sslData.clientCertConf = true;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.FOR_INITIAL;
        data.phase2EapStarted = false;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        // In FOR_INITIAL mode with session resumed, the check is relaxed.
        assertEquals("Test ForInitial Resumed No Cert Incomplete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }

    @Test
    public void testForInitialStartedIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = true;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.FOR_INITIAL;
        data.phase2EapStarted = true;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test ForInitial Started Incomplete: Expected DECISION_FAIL", EapMethodRet.DECISION_FAIL, decision);
    }

    @Test
    public void testForInitialStartedComplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;
        sm.sslData.sessionResumed = true;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.FOR_INITIAL;
        data.phase2EapStarted = true;
        data.phase2EapSuccess = true;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test ForInitial Started Complete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }

    @Test
    public void testNoAuthIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.NO_AUTH;
        data.phase2EapStarted = false;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test No Auth Incomplete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }

    @Test
    public void testNoAuthStartedIncomplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.NO_AUTH;
        data.phase2EapStarted = true;
        data.phase2EapSuccess = false;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test No Auth Started Incomplete: Expected DECISION_FAIL", EapMethodRet.DECISION_FAIL, decision);
    }

    @Test
    public void testNoAuthStartedComplete() {
        EapSm sm = new EapSm();
        sm.sslData.connectionEstablished = true;

        EapPeapData data = new EapPeapData(sm.sslData);
        data.phase2Auth = EapPeapData.NO_AUTH;
        data.phase2EapStarted = true;
        data.phase2EapSuccess = true;

        EapMethodRet ret = new EapMethodRet();
        WPABuf req = new WPABuf("dummy request");
        int decision = EapPeap.eap_tlv_process(sm, data, ret, req, false);
        assertEquals("Test No Auth Started Complete: Expected DECISION_UNCOND_SUCC", EapMethodRet.DECISION_UNCOND_SUCC, decision);
    }
}
