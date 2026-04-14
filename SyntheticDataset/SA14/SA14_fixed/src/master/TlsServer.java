package master;

import common.TlsState;
import common.HandshakeStates;
import common.HandshakeException;

public class TlsServer {
    public TlsState state;

    public TlsServer() {
        this.state = new TlsState();
    }

    public int ssl3_accept() {
        int ret = -1;
        switch (state.handshakeStage) {
            case 0:
                System.out.println("Server: Received ClientHello");
                state.handshakeStage = HandshakeStates.SSL3_ST_SR_CERT_VRFY_A;
                ret = 1;
                break;
            case HandshakeStates.SSL3_ST_SR_CERT_VRFY_A:
            case HandshakeStates.SSL3_ST_SR_CERT_VRFY_B:
                System.out.println("Server: In Cert Verify, setting CCS_OK");
                state.flags |= state.SSL3_FLAGS_CCS_OK;
                ret = ssl3_get_cert_verify();
                if (ret <= 0)
                    return ret;
                state.handshakeStage = HandshakeStates.SSL3_ST_SR_FINISHED_A;
                break;
            case HandshakeStates.SSL3_ST_SR_FINISHED_A:
            case HandshakeStates.SSL3_ST_SR_FINISHED_B:
                System.out.println("Server: In Finished, setting CCS_OK");
                state.flags |= state.SSL3_FLAGS_CCS_OK;
                ret = ssl3_get_finished();
                if (ret <= 0)
                    return ret;
                state.handshakeStage = HandshakeStates.SSL_ST_OK;
                break;
            case HandshakeStates.SSL_ST_OK:
                state.handshakeComplete = true;
                ret = 1;
                break;
            default:
                throw new HandshakeException("Server: Unknown handshake state");
        }
        return ret;
    }

    private int ssl3_get_cert_verify() {
        System.out.println("Server: ssl3_get_cert_verify() succeeded");
        return 1;
    }

    private int ssl3_get_finished() {
        System.out.println("Server: ssl3_get_finished() succeeded");
        return 1;
    }

    public void readChangeCipherSpec() {
        if ((state.flags & state.SSL3_FLAGS_CCS_OK) == 0) {
            throw new HandshakeException("Server: CCS received out-of-order!");
        }
        System.out.println("Server: CCS accepted");
        state.flags &= ~state.SSL3_FLAGS_CCS_OK;
    }

    public void readClientFinished() {
        if (state.handshakeStage != HandshakeStates.SSL_ST_OK) {
            throw new HandshakeException("Server: Not ready for Finished");
        }
        System.out.println("Server: Received Finished; handshake complete");
        state.handshakeComplete = true;
    }
}
