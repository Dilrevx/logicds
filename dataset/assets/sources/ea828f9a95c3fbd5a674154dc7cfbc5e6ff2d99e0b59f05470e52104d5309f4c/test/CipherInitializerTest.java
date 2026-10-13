import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CipherInitializerTest {

    private EvpCipherCtx ctx;
    private EvpCipher cipher;
    private Engine impl;
    private byte[] key;
    private byte[] iv;
    private int enc;
    private OsslParam[] params;

    @Before
    public void setUp() {
        ctx = new EvpCipherCtx();
        cipher = new EvpCipher();
        impl = null;
        key = new byte[16];
        iv = new byte[16];
        enc = 1;
        params = new OsslParam[0];
    }

    @Test
    public void testEvpCipherInitInternal_CipherProvNotNull() {
        ctx.setEngine(null);
        cipher.setProv(new Object());
        cipher.setOrigin(EvpCipher.Origin.METHOD);
        impl = null;
        Engine tmpImpl = null;

        CipherInitializer.FIPS_MODULE = true;

        int result = CipherInitializer.evpCipherInitInternal(ctx, cipher, impl, key, iv, enc, params);

        assertNull("FetchedCipher should be null after execution.", ctx.getFetchedCipher());
    }

    @Test
    public void testEvpCipherInitInternal_CtxCipherProvNotNull() {
        ctx.setEngine(null);
        ctx.setCipher(new EvpCipher());
        ctx.getCipher().setProv(new Object());
        ctx.getCipher().setOrigin(EvpCipher.Origin.METHOD);
        impl = null;
        Engine tmpImpl = null;

        int result = CipherInitializer.evpCipherInitInternal(ctx, null, impl, key, iv, enc, params);

        assertNull("FetchedCipher should be null after execution.", ctx.getFetchedCipher());
    }
}
