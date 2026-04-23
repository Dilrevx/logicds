import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CaptchaServiceTest {

    private CaptchaRepo repo;
    private CaptchaService service;
    private Context ctx;

    @Before
    public void setUp() {
        repo = new CaptchaRepo();
        service = new CaptchaService(repo);
        ctx = new Context();

        repo.put("req-1", "abc123");
    }

    @Test
    public void testFirstCorrectVerifySucceeds() {
        VerifyResult result = service.verifyCaptcha(ctx, "req-1", "abc123");

        assertTrue("correct captcha should verify on first use", result.isCorrect());
        assertNull("no error expected on the happy path", result.getErr());
    }

    @Test
    public void testReplayOfSameCaptchaRejected() {
        VerifyResult first = service.verifyCaptcha(ctx, "req-1", "abc123");
        assertTrue("first use should succeed", first.isCorrect());

        VerifyResult second = service.verifyCaptcha(ctx, "req-1", "abc123");

        assertFalse("captcha must be single-use: replay of same key+value must fail",
                second.isCorrect());
    }
}
