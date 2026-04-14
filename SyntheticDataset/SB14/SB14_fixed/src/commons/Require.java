package commons;

public class Require {
    public static void check(boolean cond) {
        if (!cond) throw new IllegalArgumentException();
    }
}
