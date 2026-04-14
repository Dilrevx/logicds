import org.junit.Test;
import static org.junit.Assert.*;

import commons.*;

public class HandshakeTest {

    @Test
    public void testClientHello() {
        Session session = new Session();
        Internals internals = new Internals();
        SecurityParameters secParams = new SecurityParameters();
        SecurityParameters resumedParams = new SecurityParameters();
        Priorities priorities = new Priorities();
        Dtls dtls = new Dtls();
        Datum dcookie = new Datum();


        VersionEntry minVer = new VersionEntry((byte)3, (byte)1, 0x0301, false);
        VersionEntry maxVer = new VersionEntry((byte)3, (byte)3, 0x0303, false);
        VersionEntry hver = new VersionEntry((byte)3, (byte)3, 0x0303, false);
        
        resumedParams.setProtocolVersion(hver);

        dtls.setHskHelloVerifyRequests(0);
        dtls.setDcookie(dcookie);
        dtls.getDcookie().setData(new byte[16], 16);

        internals.setResumedSecurityParameters(resumedParams);
        internals.setHskFlags(0);
        internals.setPriorities(priorities);
        internals.setDefaultHelloVersion(new byte[]{0, 0});
        internals.setDtls(dtls);
        
        session.setInternals(internals);
        session.setSecurityParameters(secParams);
        
        VersionUtils.setVersionLowest(minVer);
        VersionUtils.setVersionMax(maxVer);
        VersionUtils.setLegacyVersionMax(hver);
        
        int result = Handshake.sendClientHello(session, 0);
        System.out.println("Result: " + result);

        assertTrue("Client random should not be 0", result >= 0);
    }


    @Test
    public void testClientHello2() {
        Session session = new Session();
        Internals internals = new Internals();
        SecurityParameters secParams = new SecurityParameters();
        SecurityParameters resumedParams = new SecurityParameters();
        Priorities priorities = new Priorities();
        Dtls dtls = new Dtls();
        Datum dcookie = new Datum();


        VersionEntry minVer = new VersionEntry((byte)3, (byte)1, 0x0301, false);
        VersionEntry maxVer = new VersionEntry((byte)3, (byte)3, 0x0303, false);
        VersionEntry hver = new VersionEntry((byte)3, (byte)3, 0x0303, false);

        resumedParams.setProtocolVersion(hver);

        dtls.setHskHelloVerifyRequests(0);
        dtls.setDcookie(dcookie);
        dtls.getDcookie().setData(new byte[16], 16);

        internals.setResumedSecurityParameters(resumedParams);
        internals.setHskFlags(0x01);
        internals.setPriorities(priorities);
        internals.setDefaultHelloVersion(new byte[]{0, 0});
        internals.setDtls(dtls);

        session.setInternals(internals);
        session.setSecurityParameters(secParams);

        VersionUtils.setVersionLowest(minVer);
        VersionUtils.setVersionMax(maxVer);
        VersionUtils.setLegacyVersionMax(hver);

        int result = Handshake.sendClientHello(session, 0);
        System.out.println("Result: " + result);

        assertTrue("Result should not be < 0", result >= 0);
    }
}
