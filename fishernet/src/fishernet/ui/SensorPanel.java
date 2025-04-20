/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.ui;
import fishernet.dao.SensorDAO;
import fishernet.model.Boat;
import fishernet.model.SensorData;

import javax.swing.*;
import java.awt.*;
import java.util.List;
/**
 *
 * @author ASUS
 */
public class SensorPanel extends JPanel {

    private Boat boat;
    private JTextArea sensorOutput;
    private Timer autoRefreshTimer;

    public SensorPanel(Boat boat) {
        this.boat = boat;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Live Sensor Data: " + boat.getName()));

        sensorOutput = new JTextArea(10, 30);
        sensorOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(sensorOutput);

        JButton refreshBtn = new JButton("Manual Refresh");
        refreshBtn.addActionListener(e -> loadSensorData());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshBtn, BorderLayout.SOUTH);

        // Start auto-refresh every 5 seconds
        autoRefreshTimer = new Timer(5000, e -> loadSensorData());
        autoRefreshTimer.start();

        loadSensorData();
    }

    private void loadSensorData() {
        List<SensorData> sensorList = SensorDAO.getLatestSensorData(boat.getId());

        sensorOutput.setText(""); // clear output
        for (SensorData data : sensorList) {
            String line = String.format("%s: %s\n", data.getSensorType(), data.getValue());
            sensorOutput.append(line);

            // Optional alerts
            if (data.getSensorType().equals("water_level") && Integer.parseInt(data.getValue()) > 3) {
                sensorOutput.append("⚠️  High water level detected!\n");
            }
            if (data.getSensorType().equals("vibration") && data.getValue().equalsIgnoreCase("irregular")) {
                sensorOutput.append("⚠️  Irregular vibration detected!\n");
            }
        }
    }

    // Optional: Call this if you want to stop the timer later
    public void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }
}
