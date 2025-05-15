package dashboard;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javafx.embed.swing.JFXPanel;

public class Launcher {
    public static void main(String[] args) {
        // Set look and feel
        FlatDarkLaf.setup();

        // This line initializes JavaFX toolkit for Swing applications
        new JFXPanel(); // Must be called before any JavaFX usage

        // Start your main Swing UI
        SwingUtilities.invokeLater(() -> {
            new HomePageFrame();
        });
    }
}