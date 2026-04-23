import org.junit.Test;
import static org.junit.Assert.*;

public class X509PolicyValidatorTest {

    @Test
    public void testCheckPolicy() {
        X509StoreCtx ctx = new X509StoreCtx();
        ctx.bareTaSigned = false;
        ctx.param.flags = 0x01;
        X509Certificate cert1 = new X509Certificate(1);

        ctx.chain.add(cert1);

        int result = X509PolicyValidator.checkPolicy(ctx);
        int error = ctx.error;

        assertEquals("Expected checkPolicy() to return 1 when invalid policy is detected.", 1, result);
        assertNotEquals("Expected error to be set", 0, error);
    }

    @Test
    public void testCheckPolicy2() {
        X509StoreCtx ctx = new X509StoreCtx();
        ctx.bareTaSigned = false;
        ctx.param.flags = 0x01;

        X509Certificate cert1 = new X509Certificate(0);
        ctx.chain.add(cert1);

        int result = X509PolicyValidator.checkPolicy(ctx);
        int error = ctx.error;

        assertEquals("Expected checkPolicy() to return 0 when no invalid policies exist.", 0, result);
    }
}
