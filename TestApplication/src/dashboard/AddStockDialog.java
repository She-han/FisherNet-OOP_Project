package dashboard;

import com.github.lgooddatepicker.components.DatePicker;
import db.DBHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddStockDialog extends JDialog {
    private boolean added = false;
    private DatePicker datePicker;
    private JTextField fishLoadField;
    private JComboBox<Integer> boatIdCombo;
    private JComboBox<String> fishTypeCombo;

    private static final String[] FISH_TYPES = {"Tuna", "Salmon", "Prawns", "Crab", "Lobster", "Other"};
    private String adminName;

    public AddStockDialog(String adminName) {
        super((Frame) null, "Add Fish Stock", true);
        this.adminName = adminName;
        setLayout(new BorderLayout());
        pack();
        setResizable(false);
        setLocationRelativeTo(null); // This centers the dialog on the screen
        setBackground(new Color(40, 50, 68));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(40, 50, 68));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;

        // Date - using LGoodDatePicker
        c.gridx = 0; c.gridy = 0; form.add(styledLabel("Date (YYYY-MM-DD):"), c);
        c.gridx = 1;
        datePicker = new DatePicker();
        form.add(datePicker, c);

        // Scan QR button
        JButton scanQRBtn = new JButton("Scan QR");
        scanQRBtn.setBackground(new Color(33, 186, 99));
        scanQRBtn.setForeground(Color.WHITE);
        scanQRBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        scanQRBtn.setFocusPainted(false);
        scanQRBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        scanQRBtn.addActionListener(e -> {
            QRScannerFrame scanner = new QRScannerFrame(qrText -> {
                try {
                    int scannedId = Integer.parseInt(qrText.trim());
                    selectOrAddBoatId(scannedId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid QR content: " + qrText, "QR Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            scanner.setVisible(true);
        });

        // Boat ID + Name
        c.gridx = 0; c.gridy = 1; form.add(styledLabel("Boat ID:"), c);
        c.gridx = 1;
        boatIdCombo = new JComboBox<>();
        LinkedHashMap<Integer, String> boatMap = getBoatIdMap();
        for (Integer id : boatMap.keySet()) {
            boatIdCombo.addItem(id);
        }
        form.add(boatIdCombo, c);
        c.gridx = 2;
        form.add(scanQRBtn, c);

        // Fish type
        c.gridx = 0; c.gridy = 2; form.add(styledLabel("Fish Type:"), c);
        c.gridx = 1;
        fishTypeCombo = new JComboBox<>(FISH_TYPES);
        form.add(fishTypeCombo, c);

        // Fish load
        c.gridx = 0; c.gridy = 3; form.add(styledLabel("Fish Load (Kg):"), c);
        c.gridx = 1;
        fishLoadField = styledField("");
        form.add(fishLoadField, c);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnPanel.setOpaque(false);
        JButton addBtn = styledButton("Add", new Color(33,99,186));
        JButton cancelBtn = styledButton("Cancel", new Color(120,120,120));
        btnPanel.add(addBtn); btnPanel.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);

        // Actions
        cancelBtn.addActionListener(e -> dispose());
        addBtn.addActionListener(e -> {
            LocalDate localDate = datePicker.getDate();
            String date = (localDate != null) ? localDate.toString() : "";
            Integer boatId = (Integer) boatIdCombo.getSelectedItem();
            String fishType = (String) fishTypeCombo.getSelectedItem();
            String fishLoadStr = fishLoadField.getText().trim();

            if (date.isEmpty() || boatId == null || fishType.isEmpty() || fishLoadStr.isEmpty()) {
                AnimatedMessage.showMessage(this, "Fill all fields!", "Warning", AnimatedMessage.Type.WARNING);
                return;
            }
            double fishLoad;
            try {
                fishLoad = Double.parseDouble(fishLoadStr);
            } catch (NumberFormatException ex) {
                AnimatedMessage.showMessage(this, "Invalid fish load.", "Error", AnimatedMessage.Type.ERROR);
                return;
            }
            try (Connection con = DBHelper.getConnection()) {
                String sql = "INSERT INTO fish_stocks (date, boat_id, fish_type, fish_load_kg, updated_at, updated_by) VALUES (?, ?, ?, ?,CURRENT_TIMESTAMP,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, date);
                ps.setInt(2, boatId);
                ps.setString(3, fishType);
                ps.setDouble(4, fishLoad);
                ps.setString(5, adminName);

                ps.executeUpdate();
                AnimatedMessage.showMessage(this, "Fish stock added.", "Success", AnimatedMessage.Type.SUCCESS);
                added = true;
                dispose();
            } catch (SQLException ex) {
                if (ex.getMessage().toLowerCase().contains("primary key")) {
                    AnimatedMessage.showMessage(this, "Stock for this boat and date already exists.", "Error", AnimatedMessage.Type.ERROR);
                } else {
                    AnimatedMessage.showMessage(this, "Failed to add: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
                }
            }
        });
    }

    // Helper: select boatId if present, else add it (optional: show warning if not in database)
    public void selectOrAddBoatId(int boatId) {
        boolean found = false;
        for (int i = 0; i < boatIdCombo.getItemCount(); i++) {
            if (boatIdCombo.getItemAt(i) == boatId) {
                boatIdCombo.setSelectedItem(boatId);
                found = true;
                break;
            }
        }
        if (!found) {
            boatIdCombo.addItem(boatId);
            boatIdCombo.setSelectedItem(boatId);
            JOptionPane.showMessageDialog(this, "Boat ID " + boatId + " is not in the dropdown. Added temporarily. Please verify!", "Boat ID Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private LinkedHashMap<Integer, String> getBoatIdMap() {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT id, name FROM boats ORDER BY id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt("id"), rs.getString("name"));
            }
        } catch (Exception e) {}
        return map;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(140,180,255));
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return l;
    }

    private JTextField styledField(String val) {
        JTextField field = new JTextField(val, 18);
        field.setBackground(new Color(44, 44, 52));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(33,99,186));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return field;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    public boolean isAdded() { return added; }
}