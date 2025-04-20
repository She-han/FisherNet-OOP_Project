package dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CardPanel extends JPanel {
    private Color normalBg = new Color(44, 44, 52); // Slight blue tint
    private Color hoverBg = new Color(33, 99, 186, 230); // Blue with opacity
    private Color normalBorder = new Color(60, 80, 120);
    private Color hoverBorder = new Color(33, 99, 186);

    public CardPanel(String title, String iconText, String description) {
        setLayout(new BorderLayout());
        setBackground(normalBg);
        setBorder(BorderFactory.createLineBorder(normalBorder, 2, true));
        setPreferredSize(new Dimension(240, 110));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(iconText, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(150, 200, 255));
        iconLabel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(180, 200, 220));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(new Color(0, 0, 0, 0));
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        add(iconLabel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBg);
                setBorder(BorderFactory.createLineBorder(hoverBorder, 2, true));
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalBg);
                setBorder(BorderFactory.createLineBorder(normalBorder, 2, true));
                repaint();
            }
        });
    }
}