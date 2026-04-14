import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.Nas5g;
import commons.UniqueByteBuffer;

import java.lang.reflect.Field;

public class Nas5gReplayTest {
    private Nas5g nas;

    @Before
    public void setUp() {
        nas = new Nas5g();
    }

    @Test
    public void testFirstMessageAccepted() {
        UniqueByteBuffer pdu = new UniqueByteBuffer(new byte[] { 0x00 });
        int result = nas.write_pdu(pdu);
        assertEquals(
                "First NAS message (seq=0) should be accepted",
                Nas5g.SRSRAN_SUCCESS,
                result
        );
    }

    @Test
    public void testReplayedMessageRejected() throws Exception {
        UniqueByteBuffer pdu = new UniqueByteBuffer(new byte[] { 0x00 });

        Field ctxtField = Nas5g.class.getDeclaredField("ctxt_base");
        ctxtField.setAccessible(true);
        Object ctxtBase = ctxtField.get(nas);

        Field rxField = ctxtBase.getClass().getDeclaredField("rx_count");
        rxField.setAccessible(true);
        rxField.setInt(ctxtBase, 1);

        int result = nas.write_pdu(pdu);
        assertEquals(
                "Replayed NAS message (seq=0 < rx_count=1) should be rejected",
                Nas5g.SRSRAN_ERROR,
                result
        );
    }
}
