import commons.Commons;
import commons.DNSMessage;
import commons.DNSName;
import commons.TSIGKey;
import commons.RDataConverter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import main.DNSMessageSigner;

public class DNSMessageSignerTest {

    @Before
    public void setup() {
        RDataConverter.testError = Commons.dns_rcode_noerror;
    }

    @Test
    public void testTsigWithoutVerifiedSigFails() {
        DNSMessage msg = new DNSMessage();
        msg.fromToWire      = Commons.DNS_MESSAGE_INTENTPARSE;
        msg.sig0            = null;
        msg.tsig            = new Object();
        msg.verifyAttempted = 1;
        msg.verifiedSig     = false;
        msg.tsigstatus      = Commons.dns_rcode_noerror;
        msg.tsigkey         = new TSIGKey(new DNSName());

        DNSName signer = new DNSName();
        int result = DNSMessageSigner.dns_message_signer(msg, signer);

        assertEquals(
                "Even if tsigstatus/error == noerror, without verifiedSig we must fail",
                Commons.DNS_R_TSIGVERIFYFAILURE, result);
    }

    @Test
    public void testTsigWithVerifiedSigSucceeds() {
        DNSMessage msg = new DNSMessage();
        msg.fromToWire      = Commons.DNS_MESSAGE_INTENTPARSE;
        msg.sig0            = null;
        msg.tsig            = new Object();
        msg.verifyAttempted = 1;
        msg.verifiedSig     = true;
        msg.tsigstatus      = Commons.dns_rcode_noerror;
        msg.tsigkey         = new TSIGKey(new DNSName());

        DNSName signer = new DNSName();
        int result = DNSMessageSigner.dns_message_signer(msg, signer);

        assertEquals(
                "With verifiedSig and OK tsigstatus/error, TSIG should succeed",
                Commons.ISC_R_SUCCESS, result);
    }
}
