
package dashboard;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefAppHandlerAdapter;

import javax.swing.*;
import java.awt.*;
public class TraccarWebUISwing {

    // Call this method to open the Traccar Web UI in a new window
    public static void openInNewWindow() {
                CefApp cefApp = CefApp.getInstance(new String[]{}, new CefAppHandlerAdapter(null) {});
        CefClient client = cefApp.createClient();

        String url = "http://localhost:8082";
        CefBrowser browser = client.createBrowser(url, false, false);

        JFrame frame = new JFrame("Traccar Web UI (JavaCEF)");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());
        frame.add(browser.getUIComponent(), BorderLayout.CENTER);
        frame.setVisible(true);
    }
}