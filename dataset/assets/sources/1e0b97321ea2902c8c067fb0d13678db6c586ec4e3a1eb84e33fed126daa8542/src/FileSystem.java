import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSystem {

    public static final class ChmodCall {
        public final String path;
        public final int mode;
        public ChmodCall(String path, int mode) { this.path = path; this.mode = mode; }
    }

    public static final class MkdirCall {
        public final String path;
        public final int mode;
        public MkdirCall(String path, int mode) { this.path = path; this.mode = mode; }
    }

    public static final class OpenCall {
        public final String path;
        public final int flags;
        public final int mode;
        public OpenCall(String path, int flags, int mode) {
            this.path = path;
            this.flags = flags;
            this.mode = mode;
        }
    }

    private final Map<String, Integer> existingModes = new HashMap<String, Integer>();
    public final List<ChmodCall> chmodCalls = new ArrayList<ChmodCall>();
    public final List<MkdirCall> mkdirCalls = new ArrayList<MkdirCall>();
    public final List<OpenCall>  openCalls  = new ArrayList<OpenCall>();
    private int nextFd = 3;

    public void addDirectory(String path, int mode) {
        existingModes.put(path, mode | ScpFlags.S_IFDIR);
    }

    public void addFile(String path, int mode) {
        existingModes.put(path, mode);
    }

    public int stat(String path, ScpStat out) {
        Integer m = existingModes.get(path);
        if (m == null) {
            out.setExists(false);
            out.setMode(0);
            return -1;
        }
        out.setExists(true);
        out.setMode(m);
        return 0;
    }

    public static boolean sIsDir(int mode) {
        return (mode & ScpFlags.S_IFDIR) != 0;
    }

    public int chmod(String path, int mode) {
        chmodCalls.add(new ChmodCall(path, mode));
        return 0;
    }

    public int mkdir(String path, int mode) {
        mkdirCalls.add(new MkdirCall(path, mode));
        existingModes.put(path, mode | ScpFlags.S_IFDIR);
        return 0;
    }

    public int open(String path, int flags, int mode) {
        openCalls.add(new OpenCall(path, flags, mode));
        Integer existing = existingModes.get(path);
        if (existing != null && sIsDir(existing)) {
            return -1;
        }
        return nextFd++;
    }

    public int write(int fd, byte[] data) {
        return data == null ? 0 : data.length;
    }

    public int close(int fd) {
        return 0;
    }
}
