import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SinkTest {

    private Sink sink;
    private FileSystem fs;
    private ScpContext ctx;
    private String targ;

    @Before
    public void setUp() {
        sink = new Sink();
        fs = new FileSystem();
        targ = "/tmp/target";
        fs.addDirectory(targ, 0755);
        ctx = new ScpContext(fs);
        ctx.setPflag(true);
        ctx.setIamrecursive(true);
    }

    @Test
    public void testNormalFileAccepted() {
        ScpReader reader = new ScpReader();
        reader.queueLine("C0644 5 hello.txt");
        reader.queuePayload(new byte[]{'h', 'e', 'l', 'l', 'o'});

        int ret = sink.sink(targ, reader, ctx);

        assertEquals("normal filename should complete sink", 0, ret);
        assertEquals("exactly one file open expected", 1, fs.openCalls.size());
        assertEquals("file should be opened inside target", targ + "/hello.txt", fs.openCalls.get(0).path);
    }

    @Test
    public void testDotFilenameRejected() {
        ScpReader reader = new ScpReader();
        reader.queueLine("D0777 0 .");
        reader.queueLine("E");

        int ret = sink.sink(targ, reader, ctx);

        assertTrue("sink must not chmod the target dir via '.' filename", fs.chmodCalls.isEmpty());
        assertEquals("dot filename must terminate sink with an error", 1, ret);
    }
}
