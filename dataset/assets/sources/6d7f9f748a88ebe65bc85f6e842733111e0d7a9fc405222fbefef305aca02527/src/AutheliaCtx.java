public class AutheliaCtx {

    private final Logger logger = new Logger();
    private final Response response = new Response();
    private final Configuration configuration = new Configuration();
    private final Authorizer authorizer = new Authorizer();

    private String originalUrlRaw;
    private Exception getOriginalUrlError;
    private String xForwardedMethod = "GET";
    private String remoteIp = "127.0.0.1";
    private VerifyAuthResult preloadedVerifyAuth = new VerifyAuthResult();
    private Exception updateActivityError;

    public Logger getLogger() { return logger; }
    public Response getResponse() { return response; }
    public Configuration getConfiguration() { return configuration; }
    public Authorizer getAuthorizer() { return authorizer; }

    public void setOriginalUrlRaw(String originalUrlRaw) { this.originalUrlRaw = originalUrlRaw; }
    public void setGetOriginalUrlError(Exception e) { this.getOriginalUrlError = e; }

    public String getXForwardedMethod() { return xForwardedMethod; }
    public void setXForwardedMethod(String xForwardedMethod) { this.xForwardedMethod = xForwardedMethod; }

    public String remoteIp() { return remoteIp; }
    public void setRemoteIp(String remoteIp) { this.remoteIp = remoteIp; }

    public VerifyAuthResult getPreloadedVerifyAuth() { return preloadedVerifyAuth; }
    public void setPreloadedVerifyAuth(VerifyAuthResult v) { this.preloadedVerifyAuth = v; }

    public Exception getUpdateActivityError() { return updateActivityError; }
    public void setUpdateActivityError(Exception updateActivityError) {
        this.updateActivityError = updateActivityError;
    }

    public static final class UrlResult {
        public final TargetURL url;
        public final Exception err;
        public UrlResult(TargetURL url, Exception err) {
            this.url = url;
            this.err = err;
        }
    }

    public UrlResult getOriginalURL() {
        if (getOriginalUrlError != null) {
            return new UrlResult(null, getOriginalUrlError);
        }
        if (originalUrlRaw == null) {
            return new UrlResult(null, new Exception("missing original url"));
        }
        int schemeEnd = originalUrlRaw.indexOf("://");
        if (schemeEnd < 0) {
            return new UrlResult(null, new Exception("malformed url: " + originalUrlRaw));
        }
        String scheme = originalUrlRaw.substring(0, schemeEnd);
        String rest = originalUrlRaw.substring(schemeEnd + 3);
        int pathStart = rest.indexOf('/');
        String host = pathStart < 0 ? rest : rest.substring(0, pathStart);
        String path = pathStart < 0 ? "" : rest.substring(pathStart);
        return new UrlResult(new TargetURL(scheme, host, path), null);
    }

    public void error(Exception err, String message) {
        response.setStatus(Response.STATUS_INTERNAL_ERROR);
        response.setBody(message);
        logger.error(err);
    }

    public void replyUnauthorized() {
        response.setStatus(Response.STATUS_UNAUTHORIZED);
    }

    public void replyForbidden() {
        response.setStatus(Response.STATUS_FORBIDDEN);
    }
}
