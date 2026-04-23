public class AuthSpnego {

    public static int basicUserResult = NgxConst.NGX_OK;
    public static int spnegoBasicResult = NgxConst.NGX_OK;
    public static int headersBasicOnlyResult = NgxConst.NGX_OK;
    public static int spnegoTokenResult = NgxConst.NGX_DECLINED;
    public static int authUserGssResult = NgxConst.NGX_DECLINED;
    public static int headersResult = NgxConst.NGX_OK;
    public static String resolvedUser = "";

    public static int ngxHttpAuthBasicUser(NgxRequest r) {
        if (basicUserResult == NgxConst.NGX_OK && resolvedUser != null) {
            r.getHeadersIn().setUser(NgxStr.fromString(resolvedUser));
        }
        return basicUserResult;
    }

    public static int ngxHttpAuthSpnegoBasic(NgxRequest r, AuthContext ctx, AuthConfig alcf) {
        return spnegoBasicResult;
    }

    public static int ngxHttpAuthSpnegoHeadersBasicOnly(NgxRequest r, AuthContext ctx, AuthConfig alcf) {
        return headersBasicOnlyResult;
    }

    public static boolean ngxSpnegoAuthorizedPrincipal(NgxRequest r, NgxStr user, AuthConfig alcf) {
        String uname = user == null ? "" : user.asString();
        return alcf.getAuthorizedPrincipals().contains(uname);
    }

    public static int ngxHttpAuthSpnegoToken(NgxRequest r, AuthContext ctx) {
        return spnegoTokenResult;
    }

    public static int ngxHttpAuthSpnegoAuthUserGss(NgxRequest r, AuthContext ctx, AuthConfig alcf) {
        return authUserGssResult;
    }

    public static int ngxHttpAuthSpnegoHeaders(NgxRequest r, AuthContext ctx, NgxStr tokenOutB64, AuthConfig alcf) {
        return headersResult;
    }

    public static void resetMocks() {
        basicUserResult = NgxConst.NGX_OK;
        spnegoBasicResult = NgxConst.NGX_OK;
        headersBasicOnlyResult = NgxConst.NGX_OK;
        spnegoTokenResult = NgxConst.NGX_DECLINED;
        authUserGssResult = NgxConst.NGX_DECLINED;
        headersResult = NgxConst.NGX_OK;
        resolvedUser = "";
    }

    private AuthSpnego() {}
}
