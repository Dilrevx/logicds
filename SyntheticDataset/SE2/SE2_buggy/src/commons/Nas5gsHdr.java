package commons;

public class Nas5gsHdr {
    public int sequence_number;
    public int security_header_type;
    public int message_type;

    public static class security_header_type_opts {
        public static final int plain_5gs_nas_message = 0;
        public static final int integrity_protected = 1;
        public static final int integrity_protected_and_ciphered = 2;
        public static final int integrity_protected_with_new_5G_nas_context = 3;
        public static final int integrity_protected_and_ciphered_with_new_5G_nas_context = 4;
    }
}