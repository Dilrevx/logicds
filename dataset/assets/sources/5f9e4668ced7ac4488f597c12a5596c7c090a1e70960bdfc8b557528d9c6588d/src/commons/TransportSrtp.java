package SD1_fixed.src.commons;

public class TransportSrtp {
    public boolean bypass_srtp;
    public Callback2 rtp_cb2;
    public Callback1 rtp_cb;
    public Object user_data;
    public int keying_cnt;
    public Keying[] keying;
    public int probation_cnt;
    public Object mutex;
    public boolean session_inited;
    public boolean use_rtcp_mux;
    public Object srtp_rx_ctx;
    public SrtpCrypto tx_policy;
    public SrtpCrypto rx_policy;
}