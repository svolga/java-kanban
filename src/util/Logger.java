package util;

public final class Logger {

    private static Logger instance;
    private StringBuilder msg;

    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void writeLog(String msg) {
        this.msg.append(msg);
        System.out.println(msg);
    }
}
