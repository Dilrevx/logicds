package commons;

public class Session {
    public AuthState authstate = new AuthState();
    public static class AuthState {
        public boolean authdone = false;
    }
}