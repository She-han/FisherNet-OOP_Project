package dashboard;

import db.DBHelper;
import util.QRCodeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;

public class AddBoatPanel extends JPanel {
    private PlaceholderTextField2 nameField, regNoField, ownerField, contactField, capacityField;

    public AddBoatPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 36, 48));
        setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));

        JLabel title = new JLabel("Register New Boat", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(33,99,186));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(28));

        nameField   = createField("Boat Name");
        regNoField  = createField("Registration Number");
        capacityField   = createField("Capacity");
        ownerField  = createField("Owner Name");
        contactField= createField("Contact No");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        btnPanel.setBackground(getBackground());

        JButton clearBtn = new JButton("Clear All");
        JButton registerBtn = new JButton("Register");

        stylizeButton(clearBtn, new Color(120,120,120));
        stylizeButton(registerBtn, new Color(33,99,186));

        clearBtn.addActionListener(e -> clearFields());
        registerBtn.addActionListener(e -> registerBoat());

        btnPanel.add(clearBtn);
        btnPanel.add(registerBtn);

        add(btnPanel);
    }

    private PlaceholderTextField2 createField(String placeholder) {
        PlaceholderTextField2 f = new PlaceholderTextField2(placeholder);
        stylizeField(f);
        f.setMaximumSize(new Dimension(350, 38));
        add(f);
        add(Box.createVerticalStrut(16));
        return f;
    }

    private void stylizeField(JTextField field) {
        field.setBackground(new Color(44, 44, 52));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(33,99,186));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    }

    private void stylizeButton(JButton btn, Color bg) {
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    private void clearFields() {
        nameField.setText("");
        regNoField.setText("");
        ownerField.setText("");
        contactField.setText("");
        capacityField.setText("");
    }

    private void registerBoat() {
        String name = nameField.getText().trim();
        String regNo = regNoField.getText().trim();
        String owner = ownerField.getText().trim();
        String contact = contactField.getText().trim();
        String capacity = capacityField.getText().trim();

        if (name.isEmpty() || regNo.isEmpty() || owner.isEmpty() || contact.isEmpty() || capacity.isEmpty()) {
            AnimatedMessage.showMessage(this, "Fill all fields!", "Warning", AnimatedMessage.Type.WARNING);
            return;
        }

        try (Connection con = DBHelper.getConnection()) {
            String sql = "INSERT INTO boats (name, registration_number, owner_name, contact_no, capacity_Kg) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, regNo);
            ps.setString(3, owner);
            ps.setString(4, contact);
            ps.setString(5, capacity);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int boatId = rs.getInt(1);

                // Show QR code window after registration
                QRCodeDialog qrDialog = new QRCodeDialog(SwingUtilities.getWindowAncestor(this), boatId);
                qrDialog.setVisible(true);

                AnimatedMessage.showMessage(this, "Boat registered successfully!", "Success", AnimatedMessage.Type.SUCCESS);
                clearFields();
            }
        } catch (Exception ex) {
            AnimatedMessage.showMessage(this, "Registration failed: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
        }
    }

    /**
     * Inner dialog class for displaying and downloading the QR code.
     */
    private static class QRCodeDialog extends JDialog {
        private final int boatId;
        private BufferedImage qrImage;

        public QRCodeDialog(Window parent, int boatId) {
            super(parent, "Boat QR Code", ModalityType.APPLICATION_MODAL);
            this.boatId = boatId;

            setLayout(new BorderLayout());
            getContentPane().setBackground(new Color(30, 36, 48));

            JLabel label = new JLabel("Boat QR Code", SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setForeground(new Color(33,99,186));
            label.setBorder(BorderFactory.createEmptyBorder(14, 0, 10, 0));
            add(label, BorderLayout.NORTH);

            // Generate QR Image
            try {
                qrImage = QRCodeUtils.generateQRCodeImage(String.valueOf(boatId), 250, 250);
                JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
                qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
                qrLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                add(qrLabel, BorderLayout.CENTER);
            } catch (Exception ex) {
                add(new JLabel("QR Generation failed: " + ex.getMessage()), BorderLayout.CENTER);
            }

            JButton downloadBtn = new JButton("Download QR");
            stylizeButton(downloadBtn, new Color(33,99,186));
            downloadBtn.setFocusPainted(false);
            downloadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            downloadBtn.addActionListener(e -> downloadQR());

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(new Color(30, 36, 48));
            btnPanel.add(downloadBtn);

            add(btnPanel, BorderLayout.SOUTH);

            setSize(310, 380);
            setLocationRelativeTo(parent);
        }

        private void stylizeButton(JButton btn, Color bg) {
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
                public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
            });
        }

        private void downloadQR() {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save Boat QR Code");
            fc.setSelectedFile(new File("boat_" + boatId + "_qr.png"));
            int choice = fc.showSaveDialog(this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    javax.imageio.ImageIO.write(qrImage, "png", file);
                    AnimatedMessage.showMessage(this, "QR code saved!", "Success", AnimatedMessage.Type.SUCCESS);
                    dispose();
                } catch (Exception ex) {
                    AnimatedMessage.showMessage(this, "Failed to save QR: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
                }
            }
        }
    }
}

// --- PlaceholderTextField class ---
class PlaceholderTextField2 extends JTextField {
    private String placeholder;
    public PlaceholderTextField2(String placeholder) {
        this.placeholder = placeholder;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            g2.setColor(new Color(200, 200, 200, 180));
            Insets insets = getInsets();
            g2.drawString(placeholder, insets.left + 6, getHeight() / 2 + getFont().getSize() / 2 - 2);
            g2.dispose();
        }
    }
}