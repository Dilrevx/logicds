import commons.*;
import main.RC4_HMAC_MD5;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RC4HmacMd5SetCtxParamsTest {

    private PROV_RC4_HMAC_MD5_CTX ctx;
    private OSSL_PARAM[] params;

    @Before
    public void setUp() {
        ctx = new PROV_RC4_HMAC_MD5_CTX();

        byte[] aad = "TLSHeader".getBytes();
        byte[] secretKey = new byte[]{0x01, 0x23, 0x45, 0x67};

        params = new OSSL_PARAM[] {
                new OSSL_PARAM(OSSL_CIPHER_PARAM.KEYLEN, new byte[16], "OCTET_STRING"),
                new OSSL_PARAM(OSSL_CIPHER_PARAM.IVLEN, new byte[16], "OCTET_STRING"),
                new OSSL_PARAM(OSSL_CIPHER_PARAM.AEAD_TLS1_AAD, aad, "OCTET_STRING"),
                new OSSL_PARAM(OSSL_CIPHER_PARAM.AEAD_MAC_KEY, secretKey, "OCTET_STRING"),
                new OSSL_PARAM(OSSL_CIPHER_PARAM.TLS_VERSION, new byte[]{0x03, 0x03}, "OCTET_STRING")
        };
    }

    @Test
    public void testMacKeyShouldNotBeEqualToAAD() {
        ctx.hw = new RC4_HMAC_MD5_HW() {
            public void init_mackey(CIPHER_BASE_CTX base, byte[] data, int len) {
                base.tlsversion = (data != null && data.length > 0) ? 9999 : 0;
                base.lastMacKeyUsed = data;
            }
        };

        int result = RC4_HMAC_MD5.rc4_hmac_md5_set_ctx_params(ctx, params);

        assertEquals("Function should succeed", 1, result);

        byte[] usedMacKey = ctx.base.lastMacKeyUsed;

        assertNotEquals("MAC key must not be same as AAD", "TLSHeader", new String(usedMacKey));
    }
}
