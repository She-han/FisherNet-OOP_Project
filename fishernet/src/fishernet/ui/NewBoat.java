package fishernet.ui;

import fishernet.dao.BoatDAO;
import fishernet.model.Boat;
import fishernet.utils.QRGenerator;
import org.json.JSONObject; // Make sure org.json is in your project

import javax.swing.*;
import java.awt.*;

public class NewBoat extends JFrame {

    public NewBoat() {
        setTitle("Boat Registration");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Boat name and registration fields
        JLabel lblName = new JLabel("Boat Name:");
        JTextField tfName = new JTextField(20);
        JLabel lblReg = new JLabel("Registration No:");
        JTextField tfReg = new JTextField(20);

        // Sensor checkboxes
        JCheckBox cbGPS = new JCheckBox("GPS Tracker");
        JCheckBox cbTempHumidity = new JCheckBox("Temp & Humidity Sensor");
        JCheckBox cbWaterLevel = new JCheckBox("Water Level Sensor");
        JCheckBox cbVibration = new JCheckBox("Vibration Sensor");
        JCheckBox cbLoadCell = new JCheckBox("Load Cell (Weight)");
        JCheckBox cbPanicButton = new JCheckBox("Panic Button");

        // Group sensor checkboxes
        JPanel sensorPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        sensorPanel.setBorder(BorderFactory.createTitledBorder("Installed Sensors"));
        sensorPanel.add(cbGPS);
        sensorPanel.add(cbTempHumidity);
        sensorPanel.add(cbWaterLevel);
        sensorPanel.add(cbVibration);
        sensorPanel.add(cbLoadCell);
        sensorPanel.add(cbPanicButton);

        // Button
        JButton btnSave = new JButton("Register Boat");

        // Form layout
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(lblName);
        formPanel.add(tfName);
        formPanel.add(lblReg);
        formPanel.add(tfReg);
        formPanel.add(new JLabel()); // spacer
        formPanel.add(btnSave);

        // Frame layout
        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.NORTH);
        add(sensorPanel, BorderLayout.CENTER);

        // Save button action
        btnSave.addActionListener(e -> {
            String name = tfName.getText().trim();
            String regNo = tfReg.getText().trim();

            if (name.isEmpty() || regNo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }

            // Build sensor JSON
            JSONObject sensorJson = new JSONObject();
            sensorJson.put("gps", cbGPS.isSelected());
            sensorJson.put("tempHumidity", cbTempHumidity.isSelected());
            sensorJson.put("waterLevel", cbWaterLevel.isSelected());
            sensorJson.put("vibration", cbVibration.isSelected());
            sensorJson.put("loadCell", cbLoadCell.isSelected());
            sensorJson.put("panicButton", cbPanicButton.isSelected());
            String sensorsJsonStr = sensorJson.toString();

            // Generate QR
            String qrPath = QRGenerator.generateQR(regNo, regNo);
            if (qrPath != null) {
                Boat boat = new Boat(name, regNo, qrPath, sensorsJsonStr);
                BoatDAO.saveBoat(boat);
                JOptionPane.showMessageDialog(this, "✅ Boat registered with QR and sensor info!");

                // Clear fields
                tfName.setText("");
                tfReg.setText("");
                cbGPS.setSelected(false);
                cbTempHumidity.setSelected(false);
                cbWaterLevel.setSelected(false);
                cbVibration.setSelected(false);
                cbLoadCell.setSelected(false);
                cbPanicButton.setSelected(false);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to generate QR code.");
            }
        });
    }

    public static void main(String[] args) {
        // Optional: Use Nimbus look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Default look and feel will be used.");
        }

        SwingUtilities.invokeLater(() -> new NewBoat().setVisible(true));
    }
}
