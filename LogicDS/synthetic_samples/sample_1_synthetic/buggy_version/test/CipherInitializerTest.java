import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import commons.*;

public class CipherInitializerTest {

    private CipherContext ctx;
    private Cipher cipher;
    private Engine engine;
    private CipherParams params;

    @Before
    public void setUp() {
        cipher = new Cipher("AES");
        engine = new Engine();
        params = new CipherParams();
        ctx = new CipherContext(cipher, false, engine);

        int expectedKeyLength = 256;
        int expectedIvLength = 16;

        params.set("keylen", expectedKeyLength);
        params.set("ivlen", expectedIvLength);

        CipherInitializer.setFipsModule(false);
        CipherInitializer.setOpenSslNoEngine(true);
    }

    @Test
    public void testInitializeCipher_KeyIvLengthPersistence() {
        boolean result = CipherInitializer.initializeCipher(
                ctx, cipher, engine, new byte[32], new byte[16], 1, params
        );

        if (ctx == null) {
            System.out.println("ctx is null");
        }

        int storedKeyLen = ctx.getParam("keylen");
        int storedIvLen = ctx.getParam("ivlen");

        assertTrue("Cipher initialization should succeed", result);

        assertEquals("Key length should match the provided value", 256, storedKeyLen);
        assertEquals("IV length should match the provided value", 16, storedIvLen);
    }
}
