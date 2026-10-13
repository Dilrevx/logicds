import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.Nas;
import commons.UniqueByteBuffer;

public class NasAuthRejectTest {
    private Nas nas;

    @Before
    public void setUp() {
        nas = new Nas();
    }

    @Test
    public void testPlainAuthRejectAcceptedWithoutCtxt() {
        nas.setHaveCtxt(false);
        byte[] data = new byte[] {
                (byte)Nas.LIBLTE_MME_SECURITY_HDR_TYPE_INTEGRITY,
                (byte)Nas.LIBLTE_MME_MSG_TYPE_AUTHENTICATION_REJECT
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(data);
        int status = nas.write_pdu(1, pdu);
        assertEquals(
                "AUTHENTICATION_REJECT should be processed when no security context is established",
                Nas.SRSRAN_SUCCESS,
                status
        );
        assertEquals(
                "parseAuthenticationReject should have been invoked",
                1,
                nas.getAuthRejectCount()
        );
    }

    @Test
    public void testPlainAuthRejectRejectedInSecureState() {
        nas.setHaveCtxt(true);
        byte[] data = new byte[] {
                (byte)Nas.LIBLTE_MME_SECURITY_HDR_TYPE_INTEGRITY,
                (byte)Nas.LIBLTE_MME_MSG_TYPE_AUTHENTICATION_REJECT
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(data);
        int status = nas.write_pdu(1, pdu);
        assertEquals(
                "AUTHENTICATION_REJECT must be rejected when UE has an established security context",
                Nas.SRSRAN_ERROR,
                status
        );
        assertEquals(
                "parseAuthenticationReject must not be invoked in secure state",
                0,
                nas.getAuthRejectCount()
        );
    }
}
