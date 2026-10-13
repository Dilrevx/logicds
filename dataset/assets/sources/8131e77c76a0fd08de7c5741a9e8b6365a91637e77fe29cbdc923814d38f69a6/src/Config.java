public class Config {

    public static ServerSettings Server = new ServerSettings();

    public static void reset() {
        Server = new ServerSettings();
    }

    private Config() {}
}
