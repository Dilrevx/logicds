import common.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class OpenSslSslTest {
    private OpenSslSsl s;

    @Test
    public void testTlsPostProcessServerCertificate() {
        s = new OpenSslSsl();
        s.verify_callback = null;
        s.ctx = new SslContext();
        s.ctx.app_verify_callback = null;
        s.ctx.app_verify_arg = null;
        s.verify_mode = OpenSslSsl.SSL_VERIFY_NONE;
        s.session = new Session();
        s.session.peer_chain = new ArrayList<X509>();
        s.verified_chain = new ArrayList<X509>();
        int result = OpenSslSsl.tls_post_process_server_certificate(s, 0);
        assertEquals(WorkState.WORK_FINISHED_CONTINUE, result);
    }

    @Test
    public void test2() {
        s = new OpenSslSsl();
        s.verify_callback = null;
        s.ctx = new SslContext();
        s.ctx.app_verify_callback = null;
        s.ctx.app_verify_arg = null;
        s.verify_mode = OpenSslSsl.SSL_VERIFY_NONE;
        s.session = new Session();
        s.session.peer_chain = new ArrayList<X509>();
        s.verified_chain = new ArrayList<X509>();
        int result = OpenSslSsl.ssl_verify_cert_chain(s, new ArrayList<>());
        assertEquals(0, result);

    }


}
