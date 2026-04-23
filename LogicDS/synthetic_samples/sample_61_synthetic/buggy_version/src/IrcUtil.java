import java.util.ArrayList;
import java.util.List;

public class IrcUtil {

    public static final int LG_INFO = 1;
    public static final int CMDLOG_LOGIN = 2;
    public static final int CMDLOG_ADMIN = 3;
    public static final String LOGIN_CANCELLED_STR = "Login cancelled";

    public static final List<String> notices = new ArrayList<String>();
    public static final List<String> commands = new ArrayList<String>();
    public static final List<String> logins = new ArrayList<String>();

    public static boolean ircdOnLogoutKills = false;

    public static void slog(int level, String fmt, Object... args) {
    }

    public static void notice(String fromNick, String toNick, String fmt, Object... args) {
        notices.add(toNick + ": " + String.format(fmt.replace("%s", "%s"), args));
    }

    public static void logcommandUser(String svs, User u, int kind, String fmt, Object... args) {
        commands.add(u.getNick() + ": " + fmt);
    }

    public static boolean isSoper(MyUser mu) {
        return mu != null && mu.isSoper();
    }

    public static boolean ircdOnLogout(User u, String name) {
        return ircdOnLogoutKills;
    }

    public static void myuserLogin(String svs, User u, MyUser mu, boolean extra) {
        u.setMyuser(mu);
        mu.getLogins().add(u);
        logins.add(u.getNick() + " -> " + mu.getName());
    }

    public static void reset() {
        notices.clear();
        commands.clear();
        logins.clear();
        ircdOnLogoutKills = false;
    }

    private IrcUtil() {}
}
