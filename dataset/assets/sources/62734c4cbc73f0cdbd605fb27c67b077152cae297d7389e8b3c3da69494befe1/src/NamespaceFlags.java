public class NamespaceFlags {
    public static final int CLONE_NEWPID = 0x20000000;
    public static final int CLONE_NEWNS  = 0x00020000;
    public static final int CLONE_NEWNET = 0x40000000;

    public static final int MS_REC      = 0x4000;
    public static final int MS_SLAVE    = 0x80000;
    public static final int MS_BIND     = 0x1000;
    public static final int MS_NODEV    = 0x0004;
    public static final int MS_NOEXEC   = 0x0008;
    public static final int MS_NOSUID   = 0x0002;
    public static final int MS_RELATIME = 0x200000;

    public static final int MNT_DETACH = 2;
    public static final int O_PATH     = 010000000;

    public static final int PR_SET_PDEATHSIG = 1;
    public static final int SIGTERM          = 15;

    public static final int FUSE_LOG_ERR = 3;

    private NamespaceFlags() {}
}
