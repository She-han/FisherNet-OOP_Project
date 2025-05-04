package dashboard;

import db.DBHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class BoatDetailsPanel extends JPanel {
    private JPanel listPanel;
    private JTextField searchField;
    private JButton addBoatBtn;
    private Admin admin;

    public BoatDetailsPanel(Admin admin) {
        this.admin = admin;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 36, 48));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Top panel with title (left) and search + add (right)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Boat Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33,99,186));
        title.setBorder(new EmptyBorder(22, 0, 22, 0));
        topPanel.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);
        searchField = new JTextField(16);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(170, 32));
        searchField.setBackground(new Color(44, 44, 52));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(new Color(33,99,186));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(33,99,186));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        searchBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                searchBtn.setBackground(new Color(26, 79, 153));
            }
            public void mouseExited(MouseEvent e) {
                searchBtn.setBackground(new Color(33,99,186));
            }
        });
        searchBtn.addActionListener(e -> loadBoats());

        // --- Add New Boat Button ---
        addBoatBtn = new JButton("Add New Boat");
        addBoatBtn.setBackground(new Color(70, 120, 220));
        addBoatBtn.setForeground(Color.WHITE);
        addBoatBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addBoatBtn.setFocusPainted(false);
        addBoatBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBoatBtn.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        addBoatBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addBoatBtn.setBackground(new Color(50, 100, 180));
            }
            public void mouseExited(MouseEvent e) {
                addBoatBtn.setBackground(new Color(70, 120, 220));
            }
        });
        addBoatBtn.addActionListener(e -> showAddBoatDialog(admin));

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(addBoatBtn); // Add new boat button next to search

        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // List panel for boats
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(30, 36, 48));
        
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setBackground(new Color(30, 36, 48));
        add(scroll, BorderLayout.CENTER);

        loadBoats();
    }

    private void showAddBoatDialog(Admin admin) {
        AddBoatPanel addBoatPanel = new AddBoatPanel(admin.lastName);
        int result = JOptionPane.showConfirmDialog(
                this,
                addBoatPanel,
                "Add New Boat",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        // If the user clicks OK, reload boats (AddBoatPanel does insertion on Register)
        if (result == JOptionPane.OK_OPTION) {
            // Optionally, check if the boat was actually registered
            loadBoats();
        }
    }

    private void loadBoats() {
        listPanel.removeAll();
        ArrayList<BoatRowPanel> boatRows = new ArrayList<>();
        String search = searchField != null ? searchField.getText().trim() : "";
        try (Connection con = DBHelper.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT id, name, contact_no, status, registration_number, owner_name, gps_status FROM boats");
            if (!search.isEmpty()) {
                sql.append(" WHERE id LIKE ? OR LOWER(name) LIKE ?");
            }
            sql.append(" ORDER BY id DESC");
            PreparedStatement ps = con.prepareStatement(sql.toString());
            if (!search.isEmpty()) {
                ps.setString(1, "%" + search + "%");
                ps.setString(2, "%" + search.toLowerCase() + "%");
            }
            ResultSet rs = ps.executeQuery();
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int boatId = rs.getInt("id");
                String name = rs.getString("name");
                String contact = rs.getString("contact_no");
                String regNo = rs.getString("registration_number");
                String owner = rs.getString("owner_name");
                boolean onSail = "sail".equalsIgnoreCase(rs.getString("status"));
                String gpsStatus = rs.getString("gps_status");
                BoatRowPanel panel = new BoatRowPanel(boatId, name, contact, onSail, regNo, owner, gpsStatus);
                boatRows.add(panel);
                listPanel.add(panel);
                listPanel.add(Box.createVerticalStrut(5));
            }
            if (!hasResults) {
                JLabel emptyLabel = new JLabel("No boats found.");
                emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                emptyLabel.setForeground(new Color(130, 160, 210));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel emptyPanel = new JPanel();
                emptyPanel.setOpaque(false);
                emptyPanel.add(emptyLabel);
                listPanel.add(emptyPanel);
            }
        } catch (Exception e) {
            JLabel errLabel = new JLabel("Failed to load boats: " + e.getMessage());
            errLabel.setForeground(Color.RED);
            listPanel.add(errLabel);
        }
        revalidate();
        repaint();
    }

    // --- Custom SwitchButton for status ---
    static class SwitchButton extends JComponent {
        private boolean selected;
        private Color bgOn = new Color(33, 186, 99);
        private Color bgOff = new Color(100, 100, 120);
        private int radius = 16;
        private String textOn = "On Sail";
        private String textOff = "In Port";

        public SwitchButton(boolean selected) {
            this.selected = selected;
            setPreferredSize(new Dimension(90, 32));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelected(!isSelected());
                    fireActionPerformed();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(selected ? bgOn : bgOff);
            g2.fillRoundRect(0, 0, w, h, h, h);

            int knobX = selected ? w - h + 2 : 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(knobX, 2, h - 4, h - 4);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(selected ? bgOn.darker() : Color.WHITE);
            String text = selected ? textOn : textOff;
            int tx = selected ? 10 : h + 5;
            int ty = h / 2 + 5;
            g2.drawString(text, tx, ty);

            g2.dispose();
        }

        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        // ActionListener support
        private java.util.List<ActionListener> listeners = new ArrayList<>();
        public void addActionListener(ActionListener l) { listeners.add(l); }
        private void fireActionPerformed() {
            ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "switchChanged");
            for (ActionListener l : listeners) l.actionPerformed(evt);
        }
    }

    // --- Boat Row Panel ---
    class BoatRowPanel extends JPanel {
        int boatId;
        String regNo, ownerName, gpsStatus;
        JLabel nameLabel, idLabel, contactLabel;
        SwitchButton statusSwitch;
        JButton editBtn, deleteBtn;
        Color normalBg = new Color(40, 50, 68);
        Color hoverBg = new Color(44, 63, 104);

        public BoatRowPanel(int boatId, String name, String contact, boolean onSail, String regNo, String ownerName, String gpsStatus) {
            this.boatId = boatId;
            this.regNo = regNo;
            this.ownerName = ownerName;
            this.gpsStatus = gpsStatus;

            setBackground(normalBg);
            
            setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
            setLayout(new BorderLayout(18, 0));
            setMaximumSize(new Dimension(10000, 100));
            setPreferredSize(new Dimension(100, 100));

            // Left: id+name (row), contact (under)
            JPanel leftPanel = new JPanel();
            leftPanel.setOpaque(false);
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            JPanel idNamePanel = new JPanel();
            idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
            idNamePanel.setOpaque(false);

            idLabel = new JLabel("ID: " + boatId + "  ");
            idLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            idLabel.setForeground(new Color(33,99,186));
        
            idNamePanel.add(idLabel);

            nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
            
            contactLabel = new JLabel(contact );
            contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            contactLabel.setForeground(new Color(140, 180, 255));
            contactLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

            leftPanel.add(idNamePanel);
            leftPanel.add(nameLabel);
            leftPanel.add(contactLabel);

            add(leftPanel, BorderLayout.WEST);

            // Center: status switch
            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            centerPanel.setOpaque(false);
            statusSwitch = new SwitchButton(onSail);
            statusSwitch.addActionListener(evt -> updateStatus());
            centerPanel.add(statusSwitch);
            if ("GPS enabled".equalsIgnoreCase(gpsStatus)) {
    // You can use any icon you have, here is an example with a FontAwesome or Material icon if available,
    // or use an included image resource.
    JLabel gpsIcon = new JLabel();
    // If you have a resource, e.g. /icons/gps_on.png
    gpsIcon.setIcon(new ImageIcon(getClass().getResource("/icons/gps_on.png")));
    gpsIcon.setToolTipText("GPS Enabled");
    centerPanel.add(gpsIcon);
}
            add(centerPanel, BorderLayout.CENTER);
            

            // Right: edit/delete
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 22));
            btnPanel.setOpaque(false);
            editBtn = styledButton("Edit", new Color(60, 120, 220));
            deleteBtn = styledButton("Delete", new Color(220, 80, 80));
            btnPanel.add(editBtn);
            btnPanel.add(deleteBtn);
            add(btnPanel, BorderLayout.EAST);

            // Actions
            editBtn.addActionListener(e -> showEditDialog());
            deleteBtn.addActionListener(e -> deleteBoat());

            // Hover effect
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
                    setBackground(hoverBg);
                }
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
                    setBackground(normalBg);
                }
            });
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

        private void updateStatus() {
            boolean nowOnSail = statusSwitch.isSelected();
            String newStatus = nowOnSail ? "sail" : "port";
            try (Connection con = DBHelper.getConnection()) {
                String sql = "UPDATE boats SET status=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newStatus);
                ps.setInt(2, boatId);
                ps.executeUpdate();
                AnimatedMessage.showMessage(this, "Status updated: " + (nowOnSail ? "On Sail" : "In Port"), "Success", AnimatedMessage.Type.SUCCESS);
            } catch (Exception ex) {
                AnimatedMessage.showMessage(this, "Status update failed: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
            }
        }

        private void deleteBoat() {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this boat?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DBHelper.getConnection()) {
                    String sql = "DELETE FROM boats WHERE id=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, boatId);
                    ps.executeUpdate();
                    AnimatedMessage.showMessage(this, "Boat deleted.", "Success", AnimatedMessage.Type.SUCCESS);
                    BoatDetailsPanel.this.loadBoats();
                } catch (Exception ex) {
                    AnimatedMessage.showMessage(this, "Delete failed: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
                }
            }
        }

        private void showEditDialog() {
            BoatEditDialog dialog = new BoatEditDialog(boatId, nameLabel.getText(), regNo, ownerName, contactLabel.getText(), statusSwitch.isSelected(), gpsStatus);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.isUpdated()) {
                BoatDetailsPanel.this.loadBoats();
            }
        }
    }

    // --- Edit Dialog for Boat ---
    static class BoatEditDialog extends JDialog {
        private boolean updated = false;

        public BoatEditDialog(int boatId, String name, String regNo, String owner, String contact, boolean onSail, String gpsStatus) {
            super((Frame) null, "Edit Boat Details", true);
            setLayout(new BorderLayout());
            setBackground(new Color(40, 50, 68));
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(new Color(40, 50, 68));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(10, 10, 10, 10);
            c.anchor = GridBagConstraints.WEST;

            // Name
            c.gridx = 0; c.gridy = 0; form.add(styledLabel("Boat Name:"), c);
            c.gridx = 1; JTextField nameField = styledField(name); form.add(nameField, c);

            // Reg No
            c.gridx = 0; c.gridy = 1; form.add(styledLabel("Reg. No:"), c);
            c.gridx = 1; JTextField regNoField = styledField(regNo); form.add(regNoField, c);

            // Owner
            c.gridx = 0; c.gridy = 2; form.add(styledLabel("Owner:"), c);
            c.gridx = 1; JTextField ownerField = styledField(owner); form.add(ownerField, c);

            // Contact
            c.gridx = 0; c.gridy = 3; form.add(styledLabel("Contact:"), c);
            c.gridx = 1; JTextField contactField = styledField(contact); form.add(contactField, c);

            // Status (switch)
            c.gridx = 0; c.gridy = 4; form.add(styledLabel("Status:"), c);
            c.gridx = 1; SwitchButton statusSwitch = new SwitchButton(onSail); form.add(statusSwitch, c);

            // GPS Status dropdown (combo box)
            c.gridx = 0; c.gridy = 5; form.add(styledLabel("GPS Status:"), c);
            c.gridx = 1;
            JComboBox<String> gpsCombo = new JComboBox<>(new String[] {"GPS enabled", "GPS not enabled"});
            gpsCombo.setSelectedItem(
                (gpsStatus != null && gpsStatus.equalsIgnoreCase("GPS enabled"))
                    ? "GPS enabled"
                    : "GPS not enabled"
            );
            gpsCombo.setBackground(new Color(44, 44, 52));
            gpsCombo.setForeground(new Color(33,99,186));
            gpsCombo.setFont(new Font("Segoe UI", Font.BOLD, 14));
            form.add(gpsCombo, c);

            // Buttons
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
            btnPanel.setOpaque(false);
            JButton saveBtn = styledButton("Save", new Color(33,99,186));
            JButton cancelBtn = styledButton("Cancel", new Color(120,120,120));
            btnPanel.add(saveBtn); btnPanel.add(cancelBtn);

            // Layout
            add(form, BorderLayout.CENTER);
            add(btnPanel, BorderLayout.SOUTH);

            pack();
            setResizable(false);

            // Actions
            cancelBtn.addActionListener(e -> dispose());
            saveBtn.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String newRegNo = regNoField.getText().trim();
                String newOwner = ownerField.getText().trim();
                String newContact = contactField.getText().trim();
                String newStatus = statusSwitch.isSelected() ? "sail" : "port";
                String newGpsStatus = (String) gpsCombo.getSelectedItem();

                if (newName.isEmpty() || newRegNo.isEmpty() || newOwner.isEmpty() || newContact.isEmpty()) {
                    AnimatedMessage.showMessage(this, "Fill all fields!", "Warning", AnimatedMessage.Type.WARNING);
                    return;
                }

                try (Connection con = DBHelper.getConnection()) {
                    String sql = "UPDATE boats SET name=?, registration_number=?, owner_name=?, contact_no=?, status=?, gps_status=? WHERE id=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, newName);
                    ps.setString(2, newRegNo);
                    ps.setString(3, newOwner);
                    ps.setString(4, newContact);
                    ps.setString(5, newStatus);
                    ps.setString(6, newGpsStatus);
                    ps.setInt(7, boatId);
                    ps.executeUpdate();
                    AnimatedMessage.showMessage(this, "Boat details updated.", "Success", AnimatedMessage.Type.SUCCESS);
                    updated = true;
                    dispose();
                } catch (Exception ex) {
                    AnimatedMessage.showMessage(this, "Failed to update: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
                }
            });
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

        public boolean isUpdated() { return updated; }
    }
}