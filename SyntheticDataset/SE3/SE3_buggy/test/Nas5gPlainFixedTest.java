import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.Nas5g;
import commons.UniqueByteBuffer;
import commons.Nas5gsHdr;
import commons.MsgOpts;

public class Nas5gPlainFixedTest {
    private Nas5g nas;

    @Before
    public void setUp() {
        nas = new Nas5g();
    }

    @Test
    public void testRegistrationAcceptWithPlainHeaderRejected() {
        Nas5gsHdr.test_header_type = Nas5gsHdr.security_header_type_opts.plain_5gs_nas_message;
        byte[] pduBytes = new byte[] {
                (byte)Nas5gsHdr.security_header_type_opts.plain_5gs_nas_message,
                (byte)MsgOpts.Options.registration_accept
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(pduBytes);
        int result = nas.write_pdu(pdu);
        assertEquals(
                "Registration Accept with plain header should be rejected",
                Nas5g.SRSRAN_ERROR,
                result
        );
    }

    @Test
    public void testRegistrationAcceptWithIntegrityProtectedHeaderAccepted() {
        Nas5gsHdr.test_header_type = Nas5gsHdr.security_header_type_opts.integrity_protected_and_ciphered;
        byte[] pduBytes = new byte[] {
                (byte)Nas5gsHdr.security_header_type_opts.integrity_protected_and_ciphered,
                (byte)MsgOpts.Options.registration_accept
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(pduBytes);
        int result = nas.write_pdu(pdu);
        assertEquals(
                "Registration Accept with integrity_protected_and_ciphered header should be accepted",
                Nas5g.SRSRAN_SUCCESS,
                result
        );
    }

    @Test
    public void testConfigurationUpdateCommandWithPlainHeaderRejected() {
        Nas5gsHdr.test_header_type = Nas5gsHdr.security_header_type_opts.plain_5gs_nas_message;
        byte[] pduBytes = new byte[] {
                (byte)Nas5gsHdr.security_header_type_opts.plain_5gs_nas_message,
                (byte)MsgOpts.Options.configuration_update_command
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(pduBytes);
        int result = nas.write_pdu(pdu);
        assertEquals(
                "Configuration Update Command with plain header should be rejected",
                Nas5g.SRSRAN_ERROR,
                result
        );
    }

    @Test
    public void testConfigurationUpdateCommandWithCipheredHeaderAccepted() {
        Nas5gsHdr.test_header_type = Nas5gsHdr.security_header_type_opts.integrity_protected_and_ciphered;
        byte[] pduBytes = new byte[] {
                (byte)Nas5gsHdr.security_header_type_opts.integrity_protected_and_ciphered,
                (byte)MsgOpts.Options.configuration_update_command
        };
        UniqueByteBuffer pdu = new UniqueByteBuffer(pduBytes);
        int result = nas.write_pdu(pdu);
        assertEquals(
                "Configuration Update Command with integrity_protected_and_ciphered header should be accepted",
                Nas5g.SRSRAN_SUCCESS,
                result
        );
    }
}
