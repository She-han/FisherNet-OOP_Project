package dashboard;

import javax.swing.*;
import java.awt.*;

public class AnimatedMessage {
    public enum Type { SUCCESS, ERROR, WARNING }

    public static void showMessage(Component parent, String msg, String title, Type type) {
        Color bg, fg, border;
        int iconType = JOptionPane.INFORMATION_MESSAGE;
        switch(type) {
            case SUCCESS:
                bg = new Color(37, 160, 86);
                fg = Color.WHITE; border = new Color(33,99,186); iconType = JOptionPane.INFORMATION_MESSAGE;
                break;
            case ERROR:
                bg = new Color(200, 56, 56);
                fg = Color.WHITE; border = new Color(180, 70, 70); iconType = JOptionPane.ERROR_MESSAGE;
                break;
            default:
                bg = new Color(220, 150, 40);
                fg = Color.WHITE; border = new Color(200, 150, 60); iconType = JOptionPane.WARNING_MESSAGE;
        }
        JLabel label = new JLabel(msg, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 2, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));

        JOptionPane.showMessageDialog(parent, label, title, iconType);
    }
}