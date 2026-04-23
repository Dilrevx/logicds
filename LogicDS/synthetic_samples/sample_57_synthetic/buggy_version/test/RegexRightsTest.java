import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class RegexRightsTest {

    private RegexRights rights;

    @Before
    public void setUp() {
        Config.reset();
        Config.set("rights", "type", "owner_write");
        Config.set("rights", "file", "/etc/radicale/rights");

        rights = new RegexRights();
    }

    @Test
    public void testOwnerCanWriteOwnCollection() {
        boolean granted = rights.readFromSections("alice", "/alice/cal.ics", "r");

        assertTrue("owner must have rw on own collection", granted);
    }

    @Test
    public void testRegexMetacharUsernameRejected() {
        boolean granted = rights.readFromSections(".*", "/alice/cal.ics", "r");

        assertFalse(".* username must not match /alice/... via regex injection", granted);
    }
}
