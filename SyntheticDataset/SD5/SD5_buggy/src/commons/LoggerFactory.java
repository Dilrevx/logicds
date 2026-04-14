package commons;

public class LoggerFactory {
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz);
    }
}