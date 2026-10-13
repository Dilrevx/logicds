import org.junit.Test;
import static org.junit.Assert.*;

import common.*;
import master.*;
public class AclFileTest {
    @Test
    public void testCase_AclFileButNoRules() {
        MosquittoDb db = new MosquittoDb();
        MosquittoContext context = new MosquittoContext();

        db.config.perListenerSettings = false;
        db.config.securityOptions.aclFile = "/path/to/acl";
        db.config.securityOptions.aclList = null;
        db.config.securityOptions.aclPatterns = null;

        MosqErr result = Security_Default.mosquittoAclCheckDefault(
                db, context, "someTopic", Access.MOSQ_ACL_READ
        );

        assertEquals(MosqErr.MOSQ_ERR_ACL_DENIED, result);
    }

    @Test
    public void test2() {
        MosquittoDb db = new MosquittoDb();
        MosquittoContext context = new MosquittoContext();

        db.config.perListenerSettings = false;
        db.config.securityOptions.aclFile = "";
        db.config.securityOptions.aclList = null;
        db.config.securityOptions.aclPatterns = null;

        MosqErr result = Security_Default.mosquittoAclCheckDefault(
                db, context, "someTopic", Access.MOSQ_ACL_READ
        );

        assertEquals(MosqErr.MOSQ_ERR_ACL_DENIED, result);
    }
}

