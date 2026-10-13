import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class LdapValidatorTest {

    private LdapValidator ldapValidator;
    private LdapContext ldap;
    private Connection conn;

    @Before
    public void setUp() {
        LdapApi.resetMocks();

        ldapValidator = new LdapValidator();
        ldap = new LdapContext();
        ldap.getProps().basedn = new Buffer("ou=People,dc=example,dc=com");
        ldap.getProps().filter = new Buffer("uid");

        conn = new Connection();
        Validator v = new Validator();
        conn.setValidator(v);
    }

    @Test
    public void testValidCredentialsAccepted() {
        conn.getValidator().setUser(new Buffer("alice"));
        conn.getValidator().setPasswd(new Buffer("s3cret"));

        RetT result = ldapValidator.cherokeeValidatorLdapCheck(ldap, conn);

        assertEquals("valid creds should authenticate", RetT.OK, result);
    }

    @Test
    public void testEmptyPasswordRejected() {
        conn.getValidator().setUser(new Buffer("alice"));
        conn.getValidator().setPasswd(new Buffer(""));

        RetT result = ldapValidator.cherokeeValidatorLdapCheck(ldap, conn);

        assertEquals("empty password must be rejected (LDAP unauthenticated-bind bypass)",
                RetT.ERROR, result);
    }
}
