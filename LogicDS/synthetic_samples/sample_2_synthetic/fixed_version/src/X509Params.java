import java.util.ArrayList;
import java.util.List;

public class X509Params {
    List<String> policies;
    public int flags;

    X509Params() {
        this.policies = new ArrayList<>();
        this.flags = 0;
    }
}
