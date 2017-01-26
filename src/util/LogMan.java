package util;

public class LogMan {
    private static final boolean LOG_CRUTIAL = true;
    private static final boolean LOG_DEBUG = false;
    
    public static void logCrutial(String msg) {
        if (LOG_CRUTIAL) {
            // TODO: Persist logs in a local file.
            System.out.println(msg);
        }
    }
    
    public static void logDebug(String msg) {
        if (LOG_DEBUG) {
            // TODO: Persist logs in a local file.
            System.out.println(msg);
        }
    }
}
