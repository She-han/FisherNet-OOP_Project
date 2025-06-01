package dashboard;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;
import org.cef.CefApp; // Make sure to import this

public class MainFrame extends JFrame {
    private Admin admin;
    private JPanel contentPanel;
    private Timer sessionTimer;
    private static final int TIMEOUT = 5 * 60 * 1000;

    public MainFrame(Admin admin) {
        this.admin = admin;
        setTitle("FisherNet - Version 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);

        SidebarPanel sidebar = new SidebarPanel(admin);
        add(sidebar, BorderLayout.WEST);

        contentPanel = new ContentPanel(admin); // default panel
        add(contentPanel, BorderLayout.CENTER);

        // --- Navigation Listener setup ---
        sidebar.setNavigationListener(viewName -> {
            contentPanel.removeAll();
            switch (viewName) {
                case "dashboard":
                    contentPanel.add(new ContentPanel(admin), BorderLayout.CENTER);
                    break;
                case "boats":
                    contentPanel.add(new BoatDetailsPanel(admin), BorderLayout.CENTER);
                    break;
                case "stock":
                    contentPanel.add(new StockManagementPanel(admin.lastName), BorderLayout.CENTER);
                    break;
                case "stats":
                    contentPanel.add(new StatsPanel(), BorderLayout.CENTER);
                    break;
                case "reports":
                    contentPanel.add(new ReportsPanel(), BorderLayout.CENTER);
                    break;
                case "locations":
                    // Open TraccarWebUISwing in a new window, nothing embedded here
                    contentPanel.add(new ContentPanel(admin), BorderLayout.CENTER);
                    TraccarWebBrowser.openTraccarWebUI();
                    
                    break;
                case "admins":
                    contentPanel.add(new AdminDetailsPanel(), BorderLayout.CENTER);
                    break;
                case "logout":
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        new HomePageFrame();
                    });
                    return;
                default:
                    contentPanel.add(new ContentPanel(admin), BorderLayout.CENTER);
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        setVisible(true);
        setupSessionTimeout();
    }

    
    private void setupSessionTimeout() {
        sessionTimer = new Timer(TIMEOUT, e -> logout());
        sessionTimer.setRepeats(false);

        // Listeners to reset timer on activity:
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> sessionTimer.restart(),
            AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);

        sessionTimer.start();
    }
    
    private void logout() {
        JOptionPane.showMessageDialog(this, "Session timed out. Please log in again.");
        // Return to login screen or close app
        // Example:
        this.dispose();
        new HomePageFrame().setVisible(true);
    }
    public MainFrame() {
        this(null);
    }
}