package dashboard;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatDarkLaf;
// Remove FlatLafExtras import
// import com.formdev.flatlaf.extras.FlatLafExtras;

public class MainFrame extends JFrame {
    private Admin admin;
    private JPanel contentPanel;

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
                    contentPanel.add(new StatsPanel(), BorderLayout.CENTER); // StatsPanel now uses XChart
                    break;
                case "reports":
                    contentPanel.add(new ReportsPanel(), BorderLayout.CENTER); // StatsPanel now uses XChart
                break;
                case "admins":
                    contentPanel.add(new AdminDetailsPanel(), BorderLayout.CENTER); // StatsPanel now uses XChart
                break;
                case "logout":
                    // Dispose MainFrame and show HomePageFrame for login/signup
                    SwingUtilities.invokeLater(() -> {
                        dispose();
                        new HomePageFrame();
                    });
                    return; // Don't update contentPanel after logout
                default:
                    contentPanel.add(new ContentPanel(admin), BorderLayout.CENTER);
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        setVisible(true);
    }

    // Overload for legacy
    public MainFrame() {
        this(null);
    }

    public static void main(String[] args) {
        // Set FlatLaf look and feel
        FlatDarkLaf.setup();
        // No FlatLafExtras.installJFreeChartTheme(); needed for XChart

        // NOW start Swing app
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}