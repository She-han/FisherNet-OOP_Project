package util;

public class SessionManager {
    private static String currentAdminName;

    public static void setCurrentAdminName(String name) {
        currentAdminName = name;
    }

    public static String getCurrentAdminName() {
        return currentAdminName;
    }
}