import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.Nas;
import commons.UniqueByteBuffer;

public class NasPlainMsgTest {
    private Nas nas;

    @Before
    public void setUp() {
        nas = new Nas();
    }

    @Test
    public void testPlainEmmInformationRejected() {
        byte[] data = new byte[] {
                (byte)Nas.LIBLTE_MME_SECURITY_HDR_TYPE_PLAIN_NAS,
                (byte)Nas.LIBLTE_MME_MSG_TYPE_EMM_INFORMATION
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(data);
        int status = nas.write_pdu(1, pdu);
        assertEquals(
                "EMM Information in plain NAS should be rejected",
                Nas.SRSRAN_ERROR,
                status
        );
    }

    @Test
    public void testPlainDetachAcceptAllowed() {
        byte[] data = new byte[] {
                (byte)Nas.LIBLTE_MME_SECURITY_HDR_TYPE_PLAIN_NAS,
                (byte)Nas.LIBLTE_MME_MSG_TYPE_DETACH_ACCEPT
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(data);
        int status = nas.write_pdu(1, pdu);
        assertEquals(
                "Detach Accept in plain NAS should be accepted",
                Nas.SRSRAN_SUCCESS,
                status
        );
    }
}
