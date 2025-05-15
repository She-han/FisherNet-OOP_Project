package dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

public class SidebarPanel extends JPanel {
    private NavigationListener navListener;
    private final Color baseColor = new Color(20, 20, 48);
    private final Color hoverColor = new Color(33, 99, 186);
    private final JLabel dateTimeLabel;
    private final Admin admin;

    public SidebarPanel(Admin admin) {
        this.admin = admin;
        setBackground(baseColor);
        setPreferredSize(new Dimension(200, 0));
        setLayout(new BorderLayout());

        // MAIN vertical box for logo, admin, nav buttons
        JPanel mainBox = new JPanel();
        mainBox.setBackground(baseColor);
        mainBox.setLayout(new BoxLayout(mainBox, BoxLayout.Y_AXIS));

        // FisherNet logo at the top
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 8, 0));
        mainBox.add(logoPanel);

        // Admin display
        JPanel adminPanel = new JPanel();
        adminPanel.setBackground(new Color(30,30,72));
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.X_AXIS));
        adminPanel.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 0));

        JLabel icon = new JLabel("\uD83D\uDC64"); // 👤 emoji
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setForeground(new Color(33,99,186));
        adminPanel.add(icon);
        adminPanel.add(Box.createHorizontalStrut(10));
        String name = (admin != null && admin.lastName != null) ? admin.lastName : "Admin";
        JLabel nameLabel = new JLabel("Hi ! " + name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        nameLabel.setForeground(Color.WHITE);
        adminPanel.add(nameLabel);
        adminPanel.add(Box.createHorizontalGlue());
        mainBox.add(adminPanel);

        // Navigation buttons
        String[] items = {"Dashboard", "Boats", "Stock","Stats", "Reports", "Locations", "Admins", "Logout"};
        for (String item : items) {
            JButton btn = new JButton(item);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(baseColor);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if (navListener != null) navListener.onNavigate(item.toLowerCase());
            });
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    btn.setBackground(hoverColor);
                    btn.setForeground(Color.WHITE);
                }
                public void mouseExited(MouseEvent evt) {
                    btn.setBackground(baseColor);
                    btn.setForeground(Color.WHITE);
                }
            });

            mainBox.add(Box.createVerticalStrut(14));
            mainBox.add(btn);
        }

        // Expander glue to push time to bottom
        mainBox.add(Box.createVerticalGlue());

        // Add mainBox (logo, admin, buttons) to NORTH of BorderLayout
        add(mainBox, BorderLayout.NORTH);

        // Live date/time at bottom
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTimeLabel.setForeground(new Color(140,180,255));
        dateTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(18, 5, 18, 5));
        updateDateTime(); // Initial value

        add(dateTimeLabel, BorderLayout.SOUTH);

        // Timer to update every second
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.setRepeats(true);
        timer.start();
    }

    public SidebarPanel() {
        this(null);
    }

    // Date/time update method
    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        dateTimeLabel.setText(sdf.format(new Date()));
    }

    public void setNavigationListener(NavigationListener listener) {
        this.navListener = listener;
    }

    public interface NavigationListener {
        void onNavigate(String viewName);
    }

    // Inner class to paint logo image
    private class LogoPanel extends JPanel {
        private BufferedImage logoImage;

        public LogoPanel() {
            setBackground(new Color(20, 20, 48));
            try {
                logoImage = ImageIO.read(getClass().getResourceAsStream("/icons/logofull.png"));
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
                System.out.println("Failed to load logo image.");
            }
            setPreferredSize(new Dimension(180, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (logoImage != null) {
                int w = getWidth();
                int h = getHeight();
                // Scale image to fit nicely
                int logoWidth = Math.min(140, logoImage.getWidth());
                int logoHeight = (int) ((double) logoImage.getHeight() / logoImage.getWidth() * logoWidth);
                int logoX = (w - logoWidth) / 2;
                int logoY = (h - logoHeight) / 2;
                g.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, this);
            }
        }
    }
}