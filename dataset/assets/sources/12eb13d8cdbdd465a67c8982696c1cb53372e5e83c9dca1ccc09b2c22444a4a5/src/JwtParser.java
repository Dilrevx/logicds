public class JwtParser {

    public interface KeyFn {
        Object apply(JwtToken t) throws RuntimeException;
    }

    public static final class ParseResult {
        public final JwtToken token;
        public final RuntimeException err;
        public ParseResult(JwtToken token, RuntimeException err) {
            this.token = token;
            this.err = err;
        }
    }

    public static MockScript mockScript = new MockScript();

    public static ParseResult parseWithClaims(String tokenStr, Claims claims, KeyFn keyFn) {
        MockScript.Entry entry = mockScript.next();
        if (entry == null) {
            return new ParseResult(null, new ValidationError(ValidationError.VALIDATION_ERROR_MALFORMED, "no script entry"));
        }
        JwtToken tok = new JwtToken();
        tok.getHeader().put("alg", entry.headerAlg);
        tok.getHeader().put("kid", entry.headerKid);
        tok.setMethodAlg(entry.headerAlg);
        try {
            keyFn.apply(tok);
        } catch (RuntimeException ignored) {
        }
        claims.setSubject(entry.claimsSubject);
        claims.getAudience().clear();
        if (entry.claimsAudience != null) {
            claims.getAudience().add(entry.claimsAudience);
        }
        claims.setExpiresAtMillis(entry.claimsExpiresAtMillis);
        tok.setValid(entry.valid);
        return new ParseResult(tok, entry.err);
    }

    public static final class MockScript {
        public static final class Entry {
            public String headerAlg = "HS256";
            public String headerKid = "v1";
            public String claimsSubject = "1";
            public String claimsAudience = "user.access-token";
            public long claimsExpiresAtMillis = Long.MAX_VALUE;
            public boolean valid = true;
            public RuntimeException err = null;
        }

        private final java.util.ArrayDeque<Entry> queue = new java.util.ArrayDeque<Entry>();

        public void push(Entry e) {
            queue.addLast(e);
        }

        public Entry next() {
            return queue.pollFirst();
        }

        public void clear() {
            queue.clear();
        }
    }
}
