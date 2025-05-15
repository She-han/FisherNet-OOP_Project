package dashboard;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class QRScannerFrame extends JFrame {

    public interface QRCallback {
        void onQRRead(String qrText);
    }

    private volatile boolean running = true;
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JPanel camPanelHolder;
    private Thread scanThread;

    public QRScannerFrame(QRCallback callback) {
        setTitle("Scan Boat QR");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        List<Webcam> webcams = Webcam.getWebcams();
        if (webcams.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No webcam detected!", "Webcam Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        JComboBox<String> cameraCombo = new JComboBox<>();
        int droidCamIndex = -1;
        for (int i = 0; i < webcams.size(); i++) {
            String name = webcams.get(i).getName();
            cameraCombo.addItem(name);
            if (name.toLowerCase().contains("droidcam")) {
                droidCamIndex = i;
            }
        }
        if (droidCamIndex >= 0) {
            cameraCombo.setSelectedIndex(droidCamIndex);
        }

        webcam = webcams.get(cameraCombo.getSelectedIndex());
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setFillArea(true);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Camera:"));
        topPanel.add(cameraCombo);
        add(topPanel, BorderLayout.NORTH);

        camPanelHolder = new JPanel(new BorderLayout());
        camPanelHolder.add(webcamPanel, BorderLayout.CENTER);
        add(camPanelHolder, BorderLayout.CENTER);

        // Camera switch logic
        cameraCombo.addActionListener(e -> {
            int idx = cameraCombo.getSelectedIndex();
            running = false; // stop old scan thread
            if (webcamPanel != null) webcamPanel.stop();
            if (webcam != null && webcam.isOpen()) webcam.close();

            webcam = webcams.get(idx);
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setFillArea(true);

            camPanelHolder.removeAll();
            camPanelHolder.add(webcamPanel, BorderLayout.CENTER);
            camPanelHolder.revalidate();
            camPanelHolder.repaint();

            running = true;
            startScanThread(callback);
        });

        // Start scanning
        running = true;
        startScanThread(callback);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                running = false;
                if (webcam != null && webcam.isOpen()) webcam.close();
                if (scanThread != null) scanThread.interrupt();
            }
        });
    }

    private void startScanThread(QRCallback callback) {
        scanThread = new Thread(() -> {
            if (!webcam.isOpen()) webcam.open();
            while (running) {
                BufferedImage image = webcam.getImage();
                if (image != null) {
                    try {
                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        Result result = new MultiFormatReader().decode(bitmap);
                        if (result != null) {
                            running = false;
                            webcam.close();
                            SwingUtilities.invokeLater(() -> {
                                String qrText = result.getText();
                                JOptionPane.showMessageDialog(this, "Boat identified. Boat ID: " + qrText, "Scan Success", JOptionPane.INFORMATION_MESSAGE);
                                if (callback != null) callback.onQRRead(qrText);
                                dispose();
                            });
                            break;
                        }
                    } catch (NotFoundException e) {
                        // No QR found
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });
        scanThread.start();
    }
}