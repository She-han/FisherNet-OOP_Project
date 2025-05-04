package dashboard;

import db.DBHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BoatStatsPanel extends JPanel {

    // Card colors
    private static final Color CARD_BG = new Color(44, 52, 67);
    private static final Color CARD_TITLE = new Color(255, 255, 255);
    private static final Color CARD_NUMBER = new Color(33, 99, 186);

    public BoatStatsPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(27, 34, 44));
     //   setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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

    // Helper to create a stat card panel (no icons)
    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(18, 24, 18, 24));
        card.setPreferredSize(new Dimension(220, 120));
        card.setMaximumSize(new Dimension(220, 120));
        card.setMinimumSize(new Dimension(180, 80));
        card.setOpaque(true);

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

        // Optional shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 8, 0, new Color(27, 34, 44, 40)),
                new EmptyBorder(18, 24, 18, 24)
        ));

        return card;
    }

    // Helper to safely get single int value from DB
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
}