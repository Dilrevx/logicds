package common;

public class SSHConstants {
    public static final int SSH_KEX = 0;
    public static final int SSH_MAC_C_S = 4;
    public static final int SSH_MAC_S_C = 5;
    public static final int SSH_LANG_C_S = 8;
    public static final int SSH_KEX_METHODS = 10;
    public static final int SSH_ERROR = -1;
    public static final int SSH_OK = 0;

    public static final String KEX_EXTENSION_CLIENT = "ext-info-c";
    public static final String KEX_STRICT_SERVER = "kex-strict-s-v00@openssh.com";
    public static final String[] KEX_DESCRIPTIONS = {"KEX1", "SSH_HOSTKEYS", "SSH_CRYPT_C_S", "SSH_CRYPT_S_C", "SSH_MAC_C_S"
    , "SSH_MAC_S_C", "SSH_COMP_C_S", "SSH_COMP_S_C" , "SSH_LANG_C_S", "SSH_LANG_S_C"};
}