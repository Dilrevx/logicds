import org.junit.Test;
import static org.junit.Assert.*;
import common.TlsState;
import common.HandshakeStates;
import common.HandshakeException;

import master.*;

public class HandshakeTest {

    @Test
    public void testCompleteHandshakeFlow() {
        TlsClient client = new TlsClient();
        TlsServer server = new TlsServer();

        client.state.handshakeStage = 0;
        int retClient = client.ssl3_connect();
        assertEquals(1, retClient);

        server.state.handshakeStage = 0;
        int retServer = server.ssl3_accept();
        assertEquals(1, retServer);

        retServer = server.ssl3_accept();
        assertEquals(1, retServer);

        retServer = server.ssl3_accept();
        assertEquals(1, retServer);

        client.state.handshakeStage = HandshakeStates.SSL3_ST_CR_FINISHED_A;
        retClient = client.ssl3_connect();
        assertEquals(1, retClient);

        server.readChangeCipherSpec();
        client.readChangeCipherSpec();

        client.readServerFinished();
        server.readClientFinished();

        assertTrue("Client handshakeComplete", client.state.handshakeComplete);
        assertTrue("Server handshakeComplete", server.state.handshakeComplete);
    }

    @Test
    public void testOutOfOrderCcs_Client() {
        TlsClient client = new TlsClient();
        TlsServer server = new TlsServer();

        client.state.handshakeStage = 0;
        int retClient = client.ssl3_connect();
        assertEquals(1, retClient);

        server.state.handshakeStage = 0;
        int retServer = server.ssl3_accept();
        assertEquals(1, retServer);

        retServer = server.ssl3_accept();
        assertEquals(1, retServer);

        retServer = server.ssl3_accept();
        assertEquals(1, retServer);

        retClient = client.ssl3_connect();
        assertEquals(1, retClient);

        retClient = client.readChangeCipherSpec();
        assertEquals(1, retClient);
    }
}
