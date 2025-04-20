
package dashboard;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.setColor(new Color(200, 200, 200, 180)); // light gray, semi-transparent
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left + 4, getHeight() / 2 + getFont().getSize() / 2 - 2);
            g2.dispose();
        }
    }
}
