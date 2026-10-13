public class TargetURL {

    private String scheme;
    private String host;
    private String path;

    public TargetURL() {}

    public TargetURL(String scheme, String host, String path) {
        this.scheme = scheme;
        this.host = host;
        this.path = path;
    }

    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String stringValue() {
        return (scheme == null ? "" : scheme + "://") + (host == null ? "" : host) + (path == null ? "" : path);
    }
}
