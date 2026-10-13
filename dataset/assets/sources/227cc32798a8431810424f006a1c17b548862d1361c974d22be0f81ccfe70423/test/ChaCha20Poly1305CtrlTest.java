import commons.ChaCha20Poly1305Commons.EvpCipherCtx;
import static commons.ChaCha20Poly1305Commons.*;

import org.junit.Test;
import static org.junit.Assert.*;
import main.ChaCha20Poly1305Ctrl;

public class ChaCha20Poly1305CtrlTest {

    @Test
    public void testIvLenTooLong() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_IVLEN,
                CHACHA20_POLY1305_MAX_IVLEN + 1,
                null
        );
        assertEquals("Should reject IV length > 12", 0, result);
    }

    @Test
    public void testIvLenValid() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_IVLEN,
                CHACHA20_POLY1305_MAX_IVLEN,
                null
        );
        assertEquals("Should accept IV length = 12", 1, result);
    }

    @Test
    public void testIvLenIntermediate() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_IVLEN,
                5,
                null
        );
        assertEquals("Should accept IV length between 1 and 12", 1, result);
    }


    @Test
    public void testIvLenZero() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_IVLEN,
                0,
                null
        );
        assertEquals("Should reject IV length = 0", 0, result);
    }

    @Test
    public void testSetIvFixedInvalid() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        // arg != 12 should be rejected before ptr is used
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_IV_FIXED,
                8,
                null
        );
        assertEquals("Should reject fixed-IV length != 12", 0, result);
    }

    @Test
    public void testSetTagLengthZero() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_TAG,
                0,
                null
        );
        assertEquals("Should reject tag length = 0", 0, result);
    }

    @Test
    public void testSetTagLengthTooLarge() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_SET_TAG,
                POLY1305_BLOCK_SIZE + 1,
                null
        );
        assertEquals("Should reject tag length > POLY1305_BLOCK_SIZE", 0, result);
    }

    @Test
    public void testGetTagLengthZero() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        byte[] buf = new byte[POLY1305_BLOCK_SIZE];
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_GET_TAG,
                0,
                buf
        );
        assertEquals("Should reject get-tag length = 0", 0, result);
    }

    @Test
    public void testGetTagLengthTooLarge() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        byte[] buf = new byte[POLY1305_BLOCK_SIZE + 1];
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_GET_TAG,
                POLY1305_BLOCK_SIZE + 1,
                buf
        );
        assertEquals("Should reject get-tag length > POLY1305_BLOCK_SIZE", 0, result);
    }

    @Test
    public void testGetTagWhenNotEncrypt() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(ctx, EVP_CTRL_INIT, 0, null);
        // ctx.encrypt defaults to false
        byte[] buf = new byte[POLY1305_BLOCK_SIZE];
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_GET_TAG,
                POLY1305_BLOCK_SIZE,
                buf
        );
        assertEquals("Should reject get-tag when not in encrypt mode", 0, result);
    }

    @Test
    public void testTls1AadInvalidLength() {
        EvpCipherCtx ctx = new EvpCipherCtx();
        int result = ChaCha20Poly1305Ctrl.chacha20_poly1305_ctrl(
                ctx,
                EVP_CTRL_AEAD_TLS1_AAD,
                /* wrong length */ 5,
                null
        );
        assertEquals("Should reject TLS1 AAD when length != EVP_AEAD_TLS1_AAD_LEN", 0, result);
    }

}
