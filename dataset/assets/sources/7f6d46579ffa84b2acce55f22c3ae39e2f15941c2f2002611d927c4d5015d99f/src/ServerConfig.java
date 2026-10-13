public class ServerConfig {

    public static String getDnsKey() {
        String key = "";
        if (!Env.getenv("DNS_KEY").equals("")) {
            key = Env.getenv("DNS_KEY");
        } else if (!Config.Server.getDnsKey().equals("")) {
            key = Config.Server.getDnsKey();
        }
        return key;
    }
}
