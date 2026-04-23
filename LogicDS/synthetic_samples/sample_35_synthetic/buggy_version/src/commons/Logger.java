package commons;

public class Logger {
    private String name;
    private Logger(String name) {this.name = name;}
    public static Logger getLogger(Class<?> clazz) { return new Logger(clazz.getName()); }
    public void error(String msg, Throwable t) { }
    public void error(String msg) { }
    public void warn(String msg) { }
    public void info(String msg) { }
    public void debug(String msg) { }
}