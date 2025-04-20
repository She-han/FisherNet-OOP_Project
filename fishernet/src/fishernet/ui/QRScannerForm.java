/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 package fishernet.ui;
 import fishernet.utils.QRReader;
 import fishernet.dao.BoatDAO;
 import fishernet.model.Boat;
 import javax.swing.*;
 import java.awt.*;
 
 public class QRScannerForm extends JFrame {
    JLabel resultLabel;
    public QRScannerForm() {
        setTitle("Scan QR from File");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JButton btnScan = new JButton("Choose QR Image");
        resultLabel = new JLabel("Boat info will appear here", 
SwingConstants.CENTER);
        btnScan.addActionListener(e -> scanQR());
        setLayout(new BorderLayout());
        add(btnScan, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.CENTER);
    }
    private void scanQR() {
        JFileChooser chooser = new JFileChooser("qr");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            String regNo = QRReader.readQR(path);
            if (regNo != null) {
                Boat boat = BoatDAO.findByRegNo(regNo);
                if (boat != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Boat Found:\n" +
                            "Name: " + boat.getName() + "\n" +
                            "Reg No: " + boat.getRegistrationNo() + "\n\n" +
                            "Record stock now?",
                            "Boat Found", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        new FishStockForm(boat).setVisible(true);
                    }
                } else {
                    resultLabel.setText("No boat found for reg no: " + regNo);
                }
            } else {
                resultLabel.setText("Could not read QR code.");
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QRScannerForm().setVisible(true));
    }
 }
