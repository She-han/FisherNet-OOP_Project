package dashboard;

public class TraccarWebBrowser {
    public static void openTraccarWebUI() {
        try {
            // Full path to Chrome
            String chromePath = "\"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\"";
            // URL with --app mode
            String url = "--app=http://127.0.0.1:8082/login";
            // Build command as array (recommended to avoid quoting issues)
            String[] command = {
                "cmd", "/c", chromePath, url
            };
            // Start the process
            Process process = Runtime.getRuntime().exec(command);
            // No need to wait for it; Chrome will open independently
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}