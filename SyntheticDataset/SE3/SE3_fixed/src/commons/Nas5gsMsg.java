package commons;

import main.Nas5g;

public class Nas5gsMsg {
    public Nas5gsHdr hdr = new Nas5gsHdr();

    public int unpack_outer_hdr(UniqueByteBuffer pdu) {
        return Nas5g.SRSRAN_SUCCESS;
    }

    public int unpack(UniqueByteBuffer pdu) {
        return Nas5g.SRSRAN_SUCCESS;
    }

    public Object registration_accept() { return null; }
    public Object registration_reject() { return null; }
    public Object authentication_reject() { return null; }
    public Object authentication_request() { return null; }
    public Object identity_request() { return null; }
    public Object security_mode_command() { return null; }
    public Object service_accept() { return null; }
    public Object service_reject() { return null; }
    public Object deregistration_accept_ue_terminated() { return null; }
    public Object deregistration_request_ue_terminated() { return null; }
    public Object dl_nas_transport() { return null; }
    public Object deregistration_accept_ue_originating() { return null; }
    public Object configuration_update_command() { return null; }
}
