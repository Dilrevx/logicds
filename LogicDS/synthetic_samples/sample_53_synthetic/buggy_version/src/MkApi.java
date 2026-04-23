import java.util.Map;

public class MkApi {

    public MkPtr headerGet(Map<String, String> headersToc, String name, int nameLen) {
        if (headersToc == null) {
            return new MkPtr();
        }
        String v = headersToc.get(name);
        return new MkPtr(v);
    }

    public void headerSetHttpStatus(SessionRequest sr, int status) {
        sr.setHttpStatus(status);
    }
}
