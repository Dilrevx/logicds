import common.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SSHKeyExchangeTest {

    @Test
    public void testBuggyVsRepaired() {
        SSHKeyExchangeBuggy buggyKex = new SSHKeyExchangeBuggy(new SSHSession(false, true));
        SSHKeyExchange fixedKex = new SSHKeyExchange(new SSHSession(false, true));

        buggyKex.selectMethods();
        fixedKex.selectMethods();

        SSHCrypto buggyCrypto = buggyKex.getSSHCrypto();
        SSHCrypto fixedCrypto = fixedKex.getSSHCrypto();

        if (fixedCrypto.getKexMethod(0) == null && buggyCrypto.getKexMethod(0) == null) {
            fail("Both fixed and buggy returned null, unexpected behavior.");
        } else if (fixedCrypto.getKexMethod(0) != null && buggyCrypto.getKexMethod(0) != null &&
                   fixedCrypto.getKexMethod(0).equals(buggyCrypto.getKexMethod(0))) {
            assertNotEquals("Fixed and buggy methods should differ for insecure session.",
                            fixedCrypto.getKexMethod(0), buggyCrypto.getKexMethod(0));
        }
    }


    @Test
    public void testBuggyVsRepairedClient() {
        SSHKeyExchangeBuggy buggyKex  = new SSHKeyExchangeBuggy(new SSHSession(true, false));
        SSHKeyExchange      fixedKex  = new SSHKeyExchange     (new SSHSession(true, false));

        assertEquals("selectMethods should return OK for buggy client", SSHConstants.SSH_OK, buggyKex.selectMethods());
        assertEquals("selectMethods should return OK for fixed client", SSHConstants.SSH_OK, fixedKex.selectMethods());

        SSHCrypto buggyCrypto = buggyKex.getSSHCrypto();
        SSHCrypto fixedCrypto = fixedKex.getSSHCrypto();

        String buggyMethod = buggyCrypto.getKexMethod(SSHConstants.SSH_KEX);
        String fixedMethod = fixedCrypto.getKexMethod(SSHConstants.SSH_KEX);

        assertNotNull("Buggy client should still negotiate a method", buggyMethod);
        assertNotNull("Fixed client should still negotiate a method", fixedMethod);

        assertEquals(
                "Fixed and buggy methods should be the same for a client-only session",
                buggyMethod,
                fixedMethod
        );
    }
}

