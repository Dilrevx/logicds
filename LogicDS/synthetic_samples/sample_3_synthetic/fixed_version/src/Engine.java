public class Engine {
    public static Engine getCipherEngine(int nid) {
        return new Engine();
    }

    public static boolean init(Engine engine) {
        return true;
    }

    public static EvpCipher getCipher(Engine engine, int nid) {
        return new EvpCipher();
    }
}
