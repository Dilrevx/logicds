import java.util.ArrayList;
import java.util.List;

public class Session {

    public static final List<Shelf> added = new ArrayList<Shelf>();
    public static final List<String> deletedArchives = new ArrayList<String>();
    public static int commitCount;
    public static boolean commitThrows;
    public static Exception commitException;

    public static void add(Shelf s) {
        added.add(s);
    }

    public static void archiveDelete(int userId, String uuid) {
        deletedArchives.add(userId + ":" + uuid);
    }

    public static void commit() throws Exception {
        if (commitThrows) {
            throw commitException;
        }
        commitCount++;
    }

    public static void rollback() {
    }

    public static void reset() {
        added.clear();
        deletedArchives.clear();
        commitCount = 0;
        commitThrows = false;
        commitException = null;
    }

    private Session() {}
}
