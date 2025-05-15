package dashboard;

import db.DBHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Panel that displays statistics about boats with rounded cards.
 */
public class BoatStatsPanel extends JPanel {

    // Card colors
    private static final Color CARD_BG = new Color(50,50,72);
    private static final Color CARD_TITLE = new Color(255, 255, 255);
    private static final Color CARD_NUMBER = new Color(33, 99, 186);

    public BoatStatsPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(27, 34, 44));
        setBorder(BorderFactory.createTitledBorder("Boats Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 18, 18);
        gbc.fill = GridBagConstraints.BOTH;

        // Fetch stats from DB
        int totalBoats = getInt("SELECT COUNT(*) FROM boats");
        int onSail = getInt("SELECT COUNT(*) FROM boats WHERE status='sail'");
        int onPort = getInt("SELECT COUNT(*) FROM boats WHERE status='port'");
        int gpsEnabled = getInt("SELECT COUNT(*) FROM boats WHERE gps_status='GPS enabled'");
        int gpsDisabled = getInt("SELECT COUNT(*) FROM boats WHERE gps_status='GPS not enabled'");

        // First row: one card
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1; gbc.weighty = 0.33;
        add(createStatCard("Total Boats", String.valueOf(totalBoats)), gbc);

        // Second row: two cards
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.5; gbc.gridx = 0;
        add(createStatCard("Boats on Sail", String.valueOf(onSail)), gbc);
        gbc.gridx = 1;
        add(createStatCard("Boats on Port", String.valueOf(onPort)), gbc);

        // Third row: two cards
        gbc.gridx = 0; gbc.gridy = 2;
        add(createStatCard("GPS Enabled", String.valueOf(gpsEnabled)), gbc);
        gbc.gridx = 1;
        add(createStatCard("GPS Not Enabled", String.valueOf(gpsDisabled)), gbc);
    }

    /**
     * Helper to create a stat card panel with rounded corners.
     */
    private JPanel createStatCard(String label, String value) {
        JPanel card = new RoundedPanel(24, CARD_BG); // 24px arc radius

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 120));
        card.setMaximumSize(new Dimension(220, 120));
        card.setMinimumSize(new Dimension(180, 80));
        card.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(CARD_TITLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacer
        Component spacer = Box.createVerticalStrut(8);

        // Number
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(CARD_NUMBER);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(spacer);
        card.add(valueLabel);

        // Compound border for shadow+padding
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(33, 99, 186, 60), 0, false), // subtle colored border
                new EmptyBorder(18, 24, 18, 24)
        ));

        return card;
    }

    /**
     * Helper to safely get single int value from DB.
     */
    private int getInt(String sql) {
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * RoundedPanel - custom JPanel with rounded corners and background color.
     */
    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color bg;

        public RoundedPanel(int arc, Color bg) {
            this.arc = arc;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}