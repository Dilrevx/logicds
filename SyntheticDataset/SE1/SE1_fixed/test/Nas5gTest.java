import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.Nas5g;
import commons.UniqueByteBuffer;

import java.lang.reflect.Field;

public class Nas5gTest {
    private Nas5g nas;

    @Before
    public void setUp() {
        nas = new Nas5g();
    }

    @Test
    public void testPlainMessageBeforeSecurityContext() {
        UniqueByteBuffer pdu = new UniqueByteBuffer(new byte[] { 0, 0 });
        int result = nas.write_pdu(pdu);
        assertEquals(
                "Plain NAS message before security context should be accepted",
                Nas5g.SRSRAN_SUCCESS,
                result
        );
    }

    @Test
    public void testPlainMessageAfterSecurityContext() throws Exception {
        Field secField = Nas5g.class.getDeclaredField("has_sec_ctxt");
        secField.setAccessible(true);
        secField.setBoolean(nas, true);

        UniqueByteBuffer pdu = new UniqueByteBuffer(new byte[] { 0, 0 });
        int result = nas.write_pdu(pdu);
        assertEquals(
                "Plain NAS message after security context should be rejected",
                Nas5g.SRSRAN_ERROR,
                result
        );
    }
}
