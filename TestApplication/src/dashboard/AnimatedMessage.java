package dashboard;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AnimatedMessage {
    public enum Type { SUCCESS, ERROR, WARNING, INFO }

    public static void showMessage(Component parent, String msg, String title, Type type) {
        // Modern color palette
        Color bg, fg, border;
        int iconType;
        
        switch(type) {
            case SUCCESS:
                bg = new Color(76, 190, 80);       // Material Green
                fg = Color.WHITE;
                border = new Color(56, 142, 60);
                iconType = JOptionPane.INFORMATION_MESSAGE;
                break;
            case ERROR:
                bg = new Color(244, 67, 54);       // Material Red
                fg = Color.WHITE;
                border = new Color(211, 47, 47);
                iconType = JOptionPane.ERROR_MESSAGE;
                break;
            case INFO:
                bg = new Color(3, 169, 244);       // Material Blue
                fg = Color.WHITE;
                border = new Color(2, 136, 209);
                iconType = JOptionPane.INFORMATION_MESSAGE;
                break;
            default: // WARNING
                bg = new Color(255, 152, 0);       // Material Orange
                fg = Color.WHITE;
                border = new Color(245, 124, 0);
                iconType = JOptionPane.WARNING_MESSAGE;
        }

        // Custom styled panel with modern design
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(bg);
        
        // Message label with modern typography
        JLabel label = new JLabel(msg);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(fg);
        
        // Add padding and rounded corners
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(border, 10),
            BorderFactory.createEmptyBorder(16, 25, 16, 25)
        ));
        
        // Add the label to the panel
        panel.add(label, BorderLayout.CENTER);
        
        // Create the option pane with the custom panel
        JOptionPane optionPane = new JOptionPane(
            panel,
            iconType,
            JOptionPane.DEFAULT_OPTION
        );
        
        // Create and configure the dialog
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setResizable(false);
        
        // Set a timer to automatically close the dialog after a delay (optional)
        if (type == Type.SUCCESS || type == Type.INFO) {
            Timer autoCloseTimer = new Timer(3000, e -> dialog.dispose());
            autoCloseTimer.setRepeats(false);
            autoCloseTimer.start();
        }
        
        // Show the dialog (modal)
        dialog.setVisible(true);
    }
    
    // Custom rounded border with shadow effect
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        
        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow effect (lighter implementation)
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fill(new RoundRectangle2D.Float(x + 1, y + 1, width - 2, height - 2, radius, radius));
            
            // Draw border
            g2d.setColor(color);
            g2d.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 6, 4);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 4;
            insets.top = 4;
            insets.bottom = 6;
            return insets;
        }
    }
}