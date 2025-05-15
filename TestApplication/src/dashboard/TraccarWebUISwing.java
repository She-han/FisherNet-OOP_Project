package dashboard;

import javax.swing.*;
import java.awt.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class TraccarWebUISwing {

    public static void openInNewWindow() {
        // Create a JFrame for the browser
        JFrame frame = new JFrame("Traccar Web UI (JavaFX WebView)");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());

        // JFXPanel is the bridge between Swing and JavaFX
        JFXPanel jfxPanel = new JFXPanel();
        frame.add(jfxPanel, BorderLayout.CENTER);

        // Show frame immediately - WebView will load in background
        Point location = MouseInfo.getPointerInfo().getLocation();
        frame.setLocation(location.x + 40, location.y + 40);
        frame.setVisible(true);

        // JavaFX operations must run on the JavaFX Application Thread
        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load("http://localhost:8082/login"); // Change URL as needed

            Scene scene = new Scene(webView);
            jfxPanel.setScene(scene);
        });
    }
}