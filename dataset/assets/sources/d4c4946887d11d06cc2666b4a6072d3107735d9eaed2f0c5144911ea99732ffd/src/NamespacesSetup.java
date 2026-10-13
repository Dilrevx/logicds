public class NamespacesSetup {

    public void setupNamespaces(LoData lo, FuseSession se) {
        int child;

        if (OsApi.unshare(NamespaceFlags.CLONE_NEWPID
                | NamespaceFlags.CLONE_NEWNS
                | NamespaceFlags.CLONE_NEWNET) != 0) {
            FuseLog.log(NamespaceFlags.FUSE_LOG_ERR,
                    "unshare(CLONE_NEWPID | CLONE_NEWNS): %m\n");
            OsApi.exit(1);
        }

        child = OsApi.fork();
        if (child < 0) {
            FuseLog.log(NamespaceFlags.FUSE_LOG_ERR, "fork() failed: %m\n");
            OsApi.exit(1);
        }
        if (child > 0) {
            int waited;
            int[] wstatus = new int[]{0};

            OsApi.setupWaitParentCapabilities();

            do {
                waited = OsApi.waitpid(child, wstatus, 0);
            } while (waited < 0 && !se.isExited());

            if (se.isExited()) {
                OsApi.exit(0);
            }

            if (OsApi.wifexited(wstatus[0])) {
                OsApi.exit(OsApi.wexitstatus(wstatus[0]));
            }

            OsApi.exit(1);
        }

        OsApi.prctl(NamespaceFlags.PR_SET_PDEATHSIG, NamespaceFlags.SIGTERM);

        if (OsApi.mount(null, "/", null,
                NamespaceFlags.MS_REC | NamespaceFlags.MS_SLAVE, null) < 0) {
            FuseLog.log(NamespaceFlags.FUSE_LOG_ERR, "mount(/, MS_REC|MS_SLAVE): %m\n");
            OsApi.exit(1);
        }

        if (OsApi.mount("proc", "/proc", "proc",
                NamespaceFlags.MS_NODEV | NamespaceFlags.MS_NOEXEC
                        | NamespaceFlags.MS_NOSUID | NamespaceFlags.MS_RELATIME, null) < 0) {
            FuseLog.log(NamespaceFlags.FUSE_LOG_ERR, "mount(/proc): %m\n");
            OsApi.exit(1);
        }

        if (OsApi.mount("/proc/self/fd", "/proc", null, NamespaceFlags.MS_BIND, null) < 0) {
            FuseLog.log(NamespaceFlags.FUSE_LOG_ERR,
                    "mount(/proc/self/fd, MS_BIND): %m\n");
            OsApi.exit(1);
        }

        lo.setProcSelfFd(OsApi.open("/proc", NamespaceFlags.O_PATH));
        if (lo.getProcSelfFd() == -1) {
            FuseLog.log(NamespaceFlags.FUSE_LOG_ERR, "open(/proc, O_PATH): %m\n");
            OsApi.exit(1);
        }
    }
}
