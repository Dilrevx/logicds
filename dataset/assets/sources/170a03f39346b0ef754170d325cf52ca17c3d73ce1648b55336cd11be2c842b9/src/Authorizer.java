import java.util.List;

public class Authorizer {

    private AuthorizedResult mockResult = AuthorizedResult.AUTHORIZED;

    public AuthorizedResult isTargetURLAuthorized(TargetURL url, String username,
                                                  List<String> groups, String remoteIp,
                                                  String method, int authLevel) {
        return mockResult;
    }

    public void setMockResult(AuthorizedResult mockResult) {
        this.mockResult = mockResult;
    }
}
