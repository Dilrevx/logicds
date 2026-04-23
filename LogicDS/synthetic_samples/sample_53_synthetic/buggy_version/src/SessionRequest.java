import java.util.HashMap;
import java.util.Map;

public class SessionRequest {

    public MkPtr uri = new MkPtr();
    public MkPtr uriProcessed = new MkPtr();
    public MkPtr host = new MkPtr();
    public final Map<String, String> headersToc = new HashMap<String, String>();
    public int httpStatus;

    public MkPtr getUri() { return uri; }
    public void setUri(MkPtr uri) { this.uri = uri; }

    public MkPtr getUriProcessed() { return uriProcessed; }
    public void setUriProcessed(MkPtr uriProcessed) { this.uriProcessed = uriProcessed; }

    public MkPtr getHost() { return host; }
    public void setHost(MkPtr host) { this.host = host; }

    public Map<String, String> getHeadersToc() { return headersToc; }

    public int getHttpStatus() { return httpStatus; }
    public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }
}
