package dashboard;

import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {
    private JPanel mainPanel; // For swapping content

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 36, 48));

        // Main panel for content swapping
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Feature Cards at the top
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 24, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        topPanel.setBackground(getBackground());

        // Add Boat Card with click handler to swap panel
        CardPanel addBoatCard = new CardPanel("Add Boat", "➕", "Register a new boat");
   addBoatCard.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        // Create the popup dialog
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(ContentPanel.this);
        JDialog dialog = new JDialog(parent, "Register New Boat", true); // true = modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new AddBoatPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(parent); // Center on parent
        dialog.setVisible(true);
    }
});

        // Other cards (still as placeholders)
        CardPanel scanQRCard = new CardPanel("Scan QR", "📷", "Scan and update stock");
        CardPanel liveTrackCard = new CardPanel("Live Track", "📍", "Live boat locations");

        topPanel.add(addBoatCard);
        topPanel.add(scanQRCard);
        topPanel.add(liveTrackCard);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Show default dashboard stats on startup
        showDashboardStats();
    }

    private void showDashboardStats() {
        mainPanel.removeAll();

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 24, 24, 24));
        statsPanel.setBackground(getBackground());

        statsPanel.add(new BoatStatsPanel());
        statsPanel.add(new StockStatsPanel());

        mainPanel.add(statsPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showAddBoatPanel() {
        mainPanel.removeAll();

        AddBoatPanel addBoatPanel = new AddBoatPanel();

        // Optionally, provide a "Back" button to return to dashboard stats
        JButton backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(44, 44, 52));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> showDashboardStats());

        JPanel wrapper = new JPanel();
        wrapper.setBackground(new Color(30,36,48));
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(backBtn);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(addBoatPanel);

        mainPanel.add(wrapper, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }
}