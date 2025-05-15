import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;

public class WebcamTest {
    public static void main(String[] args) {
        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            System.out.println("No webcam detected!");
            return;
        }
        webcam.setViewSize(new Dimension(320, 240));
        WebcamPanel panel = new WebcamPanel(webcam);
        JFrame window = new JFrame("Webcam Test");
        window.add(panel);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }
}