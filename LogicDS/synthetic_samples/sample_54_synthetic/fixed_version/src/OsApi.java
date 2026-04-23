import java.util.ArrayList;
import java.util.List;

public class OsApi {

    public static int unshareResult = 0;
    public static int forkResult = 0;
    public static int waitpidStatus = 0;
    public static int prctlResult = 0;
    public static int mountResult = 0;
    public static String mkdtempResult = "virtiofsd-ABC123";
    public static int openResult = 7;
    public static int umount2Result = 0;
    public static int rmdirResult = 0;

    public static final List<MountCall> mountCalls = new ArrayList<MountCall>();
    public static final List<String> mkdtempCalls = new ArrayList<String>();
    public static final List<String> openCalls = new ArrayList<String>();
    public static final List<String> umount2Calls = new ArrayList<String>();
    public static final List<String> rmdirCalls = new ArrayList<String>();

    public static int unshare(int flags) {
        return unshareResult;
    }

    public static int fork() {
        return forkResult;
    }

    public static int waitpid(int pid, int[] wstatus, int options) {
        wstatus[0] = waitpidStatus;
        return pid;
    }

    public static boolean wifexited(int status) {
        return (status & 0x7f) == 0;
    }

    public static int wexitstatus(int status) {
        return (status >> 8) & 0xff;
    }

    public static int prctl(int option, int arg2) {
        return prctlResult;
    }

    public static int mount(String source, String target, String fstype, int flags, Object data) {
        mountCalls.add(new MountCall(source, target, fstype, flags));
        return mountResult;
    }

    public static String mkdtemp(String template) {
        mkdtempCalls.add(template);
        return mkdtempResult;
    }

    public static int open(String path, int flags) {
        openCalls.add(path);
        return openResult;
    }

    public static int umount2(String target, int flags) {
        umount2Calls.add(target);
        return umount2Result;
    }

    public static int rmdir(String path) {
        rmdirCalls.add(path);
        return rmdirResult;
    }

    public static void setupWaitParentCapabilities() {
    }

    public static void exit(int code) {
        throw new SystemExitException(code);
    }

    public static void resetMocks() {
        unshareResult = 0;
        forkResult = 0;
        waitpidStatus = 0;
        prctlResult = 0;
        mountResult = 0;
        mkdtempResult = "virtiofsd-ABC123";
        openResult = 7;
        umount2Result = 0;
        rmdirResult = 0;
        mountCalls.clear();
        mkdtempCalls.clear();
        openCalls.clear();
        umount2Calls.clear();
        rmdirCalls.clear();
    }

    private OsApi() {}
}
