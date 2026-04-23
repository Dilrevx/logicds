import java.util.ArrayDeque;
import java.util.Deque;

public class ScpReader {

    private final Deque<String> lines = new ArrayDeque<String>();
    private final Deque<byte[]> payloads = new ArrayDeque<byte[]>();

    public void queueLine(String line) {
        lines.addLast(line);
    }

    public void queuePayload(byte[] data) {
        payloads.addLast(data);
    }

    public String readLine() {
        return lines.pollFirst();
    }

    public byte[] readPayload(int expected) {
        byte[] data = payloads.pollFirst();
        if (data == null) {
            return new byte[0];
        }
        if (data.length == expected) {
            return data;
        }
        byte[] out = new byte[expected];
        System.arraycopy(data, 0, out, 0, Math.min(expected, data.length));
        return out;
    }

    public boolean hasMore() {
        return !lines.isEmpty();
    }
}
