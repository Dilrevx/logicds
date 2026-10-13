package commons;

public class SecurityModeCommand {
    public SelectedNasSecurityAlgorithms selected_nas_security_algorithms = new SelectedNasSecurityAlgorithms();
    public Object replayed_ue_security_capabilities;

    public static class SelectedNasSecurityAlgorithms {
        public Algorithm ciphering_algorithm = new Algorithm();
        public Algorithm integrity_protection_algorithm = new Algorithm();
    }

    public static class Algorithm {
        public int value;
    }
}