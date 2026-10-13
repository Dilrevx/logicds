import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.Nas;
import commons.UniqueByteBuffer;
import commons.Pcap;

import java.lang.reflect.Field;

public class NasWritePduTest {
    private Nas nas;
    private boolean writeCalled;

    @Before
    public void setUp() throws Exception {
        nas = new Nas();
        writeCalled = false;
        Pcap stubPcap = new Pcap() {
            @Override
            public void writeNas(byte[] msg, int n) {
                writeCalled = true;
            }
        };
        Field pcapField = Nas.class.getDeclaredField("pcap");
        pcapField.setAccessible(true);
        pcapField.set(nas, stubPcap);
    }

    @Test
    public void testRejectIntegrityAndCipheredWithNewContext() {
        byte[] data = new byte[] {
                (byte)Nas.LIBLTE_MME_SECURITY_HDR_TYPE_INTEGRITY_AND_CIPHERED_WITH_NEW_EPS_SECURITY_CONTEXT,
                (byte)Nas.LIBLTE_MME_MSG_TYPE_ATTACH_ACCEPT
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(data);
        nas.write_pdu(1, pdu);
        assertFalse(
                "PDU with INTEGRITY_AND_CIPHERED_WITH_NEW_EPS_SECURITY_CONTEXT should be rejected",
                writeCalled
        );
    }

    @Test
    public void testAcceptIntegrityAndCiphered() {
        writeCalled = false;
        byte[] data = new byte[] {
                (byte)Nas.LIBLTE_MME_SECURITY_HDR_TYPE_INTEGRITY_AND_CIPHERED,
                (byte)Nas.LIBLTE_MME_MSG_TYPE_ATTACH_ACCEPT
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(data);
        nas.write_pdu(1, pdu);
        assertTrue(
                "PDU with INTEGRITY_AND_CIPHERED should be accepted",
                writeCalled
        );
    }
}
