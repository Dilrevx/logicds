package commons;

public class MsgOpts {
    public static class Options {
        public static final int registration_accept = 0;
        public static final int registration_reject = 1;
        public static final int authentication_reject = 2;
        public static final int authentication_request = 3;
        public static final int identity_request = 4;
        public static final int security_mode_command = 5;
        public static final int service_accept = 6;
        public static final int service_reject = 7;
        public static final int deregistration_accept_ue_terminated = 8;
        public static final int deregistration_request_ue_terminated = 9;
        public static final int dl_nas_transport = 10;
        public static final int deregistration_accept_ue_originating = 11;
        public static final int configuration_update_command = 12;
    }
}
