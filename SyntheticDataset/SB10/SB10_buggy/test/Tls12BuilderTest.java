import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import main.Tls12Builder;
import commons.*;

public class Tls12BuilderTest {
    private TlsContext ssl;
    private byte[]     input;
    private byte[]     output;
    private final int  BLOCK = CommonsConstants.BLOCK_SIZE;

    @Before
    public void setUp() {
        ssl = new TlsContext();
        ssl.options.tls1_1 = true;
        ssl.rng = new DeterministicRng((byte)0x5A);
        input  = new byte[16];
        output = new byte[32];
    }

    @Test
    public void testIvIsNotZeroed() {
        int sz = Tls12Builder.BuildMessage(
                ssl,
                output,
                output.length,
                input,
                input.length,
                0,
                0,
                0,
                0,
                0
        );
        assertEquals(
                "Returned size must equal input.len + IV size",
                input.length + BLOCK,
                sz
        );
        assertNotNull("IV should be set in ssl.lastIv", ssl.lastIv);
        assertEquals("IV length must match block size", BLOCK, ssl.lastIv.length);
        boolean found = false;
        for (byte b : ssl.lastIv) {
            if ((b & 0xFF) == 0x5A) {
                found = true;
                break;
            }
        }
        assertTrue(
                "IV must not be all zeros after generation",
                found
        );
    }

    static class DeterministicRng extends Rng {
        private final byte pattern;
        DeterministicRng(byte pattern) { this.pattern = pattern; }
        @Override
        public int generateBlock(byte[] buf, int len) {
            for (int i = 0; i < len; i++) buf[i] = pattern;
            return 0;
        }
    }
}
