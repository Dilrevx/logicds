import java.util.ArrayList;
import java.util.List;

public class MosquittoConfig {

    private final List<MosquittoListener> listeners = new ArrayList<MosquittoListener>();
    private int listenerCount = 0;
    private final MosquittoListener defaultListener = new MosquittoListener();
    private String user;
    private int logType;
    private boolean daemon;

    public List<MosquittoListener> getListeners() { return listeners; }

    public int getListenerCount() { return listenerCount; }
    public void setListenerCount(int listenerCount) { this.listenerCount = listenerCount; }
    public void incListenerCount() { this.listenerCount++; }

    public MosquittoListener getDefaultListener() { return defaultListener; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public int getLogType() { return logType; }
    public void setLogType(int logType) { this.logType = logType; }

    public boolean isDaemon() { return daemon; }
    public void setDaemon(boolean daemon) { this.daemon = daemon; }
}
