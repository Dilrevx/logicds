import java.util.ArrayList;
import java.util.List;

public class Claims {

    private String subject = "";
    private final List<String> audience = new ArrayList<String>();
    private long expiresAtMillis;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public List<String> getAudience() { return audience; }

    public long getExpiresAtMillis() { return expiresAtMillis; }
    public void setExpiresAtMillis(long expiresAtMillis) { this.expiresAtMillis = expiresAtMillis; }
}
