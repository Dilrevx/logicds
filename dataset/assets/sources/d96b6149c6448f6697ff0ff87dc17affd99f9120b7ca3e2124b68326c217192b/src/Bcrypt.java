import java.util.HashMap;
import java.util.Map;

public class Bcrypt {

    private static final Map<String, String> hashToPlaintext = new HashMap<String, String>();

    public static void registerHash(String hash, String plaintext) {
        hashToPlaintext.put(hash, plaintext);
    }

    public static Exception compareHashAndPassword(byte[] hashedPassword, byte[] password) {
        String hash = new String(hashedPassword);
        String pass = new String(password);
        String expected = hashToPlaintext.get(hash);
        if (expected != null && expected.equals(pass)) {
            return null;
        }
        return new Exception("bcrypt: mismatch");
    }

    public static void reset() {
        hashToPlaintext.clear();
    }

    private Bcrypt() {}
}
