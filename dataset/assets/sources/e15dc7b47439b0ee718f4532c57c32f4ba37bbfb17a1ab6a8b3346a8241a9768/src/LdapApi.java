public class LdapApi {

    public static final int LDAP_SUCCESS = 0;
    public static final int LDAP_SCOPE_SUBTREE = 2;
    public static final String LDAP_NO_ATTRS = "1.1";

    public static int searchStatus = LDAP_SUCCESS;
    public static int entryCount = 1;
    public static String firstEntryDn = "uid=alice,ou=People,dc=example,dc=com";
    public static boolean bindSucceedsOnEmptyPassword = true;
    public static int unbindStatus = LDAP_SUCCESS;

    public static int searchS(Object conn, String basedn, int scope, String filter,
                              String[] attrs, int attrsOnly, LdapMessage[] messageOut) {
        messageOut[0] = new LdapMessage();
        messageOut[0].entries = entryCount;
        messageOut[0].firstDn = firstEntryDn;
        return searchStatus;
    }

    public static int countEntries(Object conn, LdapMessage message) {
        return message == null ? 0 : message.entries;
    }

    public static LdapMessage firstEntry(Object conn, LdapMessage message) {
        return message;
    }

    public static String getDn(Object conn, LdapMessage first) {
        return first == null ? null : first.firstDn;
    }

    public static void msgFree(LdapMessage message) {
    }

    public static RetT validateDn(LdapProps props, String dn, String passwd) {
        if (passwd == null) {
            return RetT.ERROR;
        }
        if (passwd.length() == 0 && !bindSucceedsOnEmptyPassword) {
            return RetT.ERROR;
        }
        return RetT.OK;
    }

    public static int unbindS(Object conn) {
        return unbindStatus;
    }

    public static RetT initFilter(LdapContext ldap, LdapProps props, Connection conn) {
        ldap.filter = new Buffer("(uid=" + conn.getValidator().getUser().buf + ")");
        return RetT.OK;
    }

    public static void resetMocks() {
        searchStatus = LDAP_SUCCESS;
        entryCount = 1;
        firstEntryDn = "uid=alice,ou=People,dc=example,dc=com";
        bindSucceedsOnEmptyPassword = true;
        unbindStatus = LDAP_SUCCESS;
    }

    private LdapApi() {}
}
