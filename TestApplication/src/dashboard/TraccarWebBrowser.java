package dashboard;

import java.awt.Desktop;
import java.net.URI;

public class TraccarWebBrowser {
    public static void openTraccarWebUI() {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:8082/login")); // Change URL as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}