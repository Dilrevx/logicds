import static org.junit.Assert.*;
import org.junit.Test;

import main.Nas5g;
import commons.SecurityModeCommand;
import commons.UniqueByteBuffer;

public class Nas5gSecurityModeCommandTest {

    @Test
    public void testProhibitNia0Algorithm() {
        Nas5g nas = new Nas5g();
        SecurityModeCommand cmd = new SecurityModeCommand();
        cmd.selected_nas_security_algorithms.integrity_protection_algorithm.value = 0;
        UniqueByteBuffer pdu = new UniqueByteBuffer(new byte[] { 0x00 });
        int result = nas.handle_security_mode_command(cmd, pdu);
        assertEquals(
                "Security Mode Command with integ_algo=0 (NIA0) should be rejected",
                Nas5g.SRSRAN_ERROR,
                result
        );
    }
}
