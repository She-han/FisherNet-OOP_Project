package dashboard;

import db.DBHelper;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class AdminDetailsPanel extends JPanel {
    private JTextField searchField;
    private JTable adminTable;
    private AdminTableModel adminTableModel;
    private JButton editButton, deleteButton;

    public AdminDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30,36,48));
        setBorder(BorderFactory.createEmptyBorder(24,24,24,24));

        // === Title at the top ===
        JLabel title = new JLabel("Admin Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33,99,186));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,18,0));
        title.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.WEST);

        // === Search bar and Edit/Delete buttons at top-right ===
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(new Color(44,44,52));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(new Color(33,99,186));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60,80,120), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchField.setPreferredSize(new Dimension(250, 36));
        searchField.setMaximumSize(new Dimension(250, 36));
        searchField.setToolTipText("Search by name or username...");

        editButton = new JButton("Edit");
        styleButton(editButton, new Color(33,99,186));
        editButton.setEnabled(false);

        deleteButton = new JButton("Delete");
        styleButton(deleteButton, new Color(200,60,60));
        deleteButton.setEnabled(false);

        rightPanel.add(Box.createHorizontalStrut(8));
        rightPanel.add(searchField);
        rightPanel.add(Box.createHorizontalStrut(12));
        rightPanel.add(editButton);
        rightPanel.add(Box.createHorizontalStrut(8));
        rightPanel.add(deleteButton);

        titlePanel.add(rightPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // === Admin Table ===
        adminTableModel = new AdminTableModel();
        adminTable = new JTable(adminTableModel);
        adminTable.setRowHeight(32);
        adminTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        adminTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminTable.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));
        adminTable.setShowGrid(false);
        adminTable.setIntercellSpacing(new Dimension(0,0));
        adminTable.setBackground(new Color(44,44,52));
        adminTable.setForeground(Color.WHITE);
        adminTable.setFillsViewportHeight(true);

        JTableHeader header = adminTable.getTableHeader();
        header.setBackground(new Color(33,99,186));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));

        JScrollPane scrollPane = new JScrollPane(adminTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(30,36,48));
        add(scrollPane, BorderLayout.CENTER);

        // === Load admins on start ===
        loadAdmins("");

        // === Search functionality ===
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchAdmins(); }
            public void removeUpdate(DocumentEvent e) { searchAdmins(); }
            public void changedUpdate(DocumentEvent e) { searchAdmins(); }
            private void searchAdmins() {
                String text = searchField.getText().trim();
                loadAdmins(text);
            }
        });

        // === Enable edit/delete button when a row is selected ===
        adminTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = adminTable.getSelectedRow() != -1;
            editButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        // === Edit dialog ===
        editButton.addActionListener(e -> openEditDialog());

        // === Delete admin ===
        deleteButton.addActionListener(e -> deleteSelectedAdmin());
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10,24,10,24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void loadAdmins(String query) {
        java.util.List<Admin> admins = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, username, email FROM admins";
        if (query != null && !query.isEmpty()) {
            sql += " WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ?";
        }
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (query != null && !query.isEmpty()) {
                String pattern = "%" + query + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                admins.add(new Admin(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        null // Password not shown here
                ));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading admins: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        adminTableModel.setAdmins(admins);
    }

    private void openEditDialog() {
        int row = adminTable.getSelectedRow();
        if (row == -1) return;
        Admin admin = adminTableModel.getAdminAt(row);
        EditAdminDialog dialog = new EditAdminDialog(admin);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            updateAdmin(dialog.getAdmin());
        }
    }

    private void updateAdmin(Admin admin) {
        String sql = "UPDATE admins SET first_name=?, last_name=?, username=?, email=? WHERE id=?";
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, admin.firstName);
            ps.setString(2, admin.lastName);
            ps.setString(3, admin.username);
            ps.setString(4, admin.email);
            ps.setInt(5, admin.id);
            ps.executeUpdate();
            loadAdmins(searchField.getText().trim());
            JOptionPane.showMessageDialog(this, "Admin updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update admin: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedAdmin() {
        int row = adminTable.getSelectedRow();
        if (row == -1) return;
        Admin admin = adminTableModel.getAdminAt(row);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete admin '" + admin.username + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM admins WHERE id=?";
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, admin.id);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                loadAdmins(searchField.getText().trim());
                JOptionPane.showMessageDialog(this, "Admin deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete admin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete admin: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Admin class with ID
    static class Admin {
        int id;
        String firstName, lastName, username, email, password;
        Admin(int id, String f, String l, String u, String e, String p) {
            this.id = id; this.firstName = f; this.lastName = l; this.username = u; this.email = e; this.password = p;
        }
    }

    // Table model for admins
    static class AdminTableModel extends AbstractTableModel {
        private String[] columns = {"ID", "First Name", "Last Name", "Username", "Email"};
        private java.util.List<Admin> admins = new ArrayList<>();

        public void setAdmins(java.util.List<Admin> list) {
            this.admins = list;
            fireTableDataChanged();
        }

        public Admin getAdminAt(int row) {
            return admins.get(row);
        }

        @Override public int getRowCount() { return admins.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public Object getValueAt(int row, int col) {
            Admin a = admins.get(row);
            switch(col) {
                case 0: return a.id;
                case 1: return a.firstName;
                case 2: return a.lastName;
                case 3: return a.username;
                case 4: return a.email;
                default: return "";
            }
        }
        @Override public boolean isCellEditable(int row, int col) { return false; }
    }

    // Edit dialog
    static class EditAdminDialog extends JDialog {
        private JTextField firstNameField, lastNameField, usernameField, emailField;
        private boolean saved = false;
        private Admin admin;

        EditAdminDialog(Admin admin) {
            setTitle("Edit Admin");
            setModal(true);
            setSize(360, 400);
            setLayout(new BorderLayout());
            setResizable(false);

            JPanel form = new JPanel();
            form.setBackground(new Color(34,45,65));
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBorder(BorderFactory.createEmptyBorder(24, 36, 12, 36));

            Font labelFont = new Font("Segoe UI", Font.BOLD, 15);
            Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

            form.add(styledLabel("First Name", labelFont));
            firstNameField = styledField(admin.firstName, fieldFont);
            form.add(firstNameField);

            form.add(Box.createVerticalStrut(12));
            form.add(styledLabel("Last Name", labelFont));
            lastNameField = styledField(admin.lastName, fieldFont);
            form.add(lastNameField);

            form.add(Box.createVerticalStrut(12));
            form.add(styledLabel("Username", labelFont));
            usernameField = styledField(admin.username, fieldFont);
            form.add(usernameField);

            form.add(Box.createVerticalStrut(12));
            form.add(styledLabel("Email", labelFont));
            emailField = styledField(admin.email, fieldFont);
            form.add(emailField);

            add(form, BorderLayout.CENTER);

            // Save button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(34,45,65));
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            JButton saveBtn = new JButton("Save");
            saveBtn.setBackground(new Color(33,99,186));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            saveBtn.setFocusPainted(false);
            saveBtn.setBorder(BorderFactory.createEmptyBorder(10,24,10,24));
            saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            saveBtn.addActionListener(e -> {
                admin.firstName = firstNameField.getText().trim();
                admin.lastName = lastNameField.getText().trim();
                admin.username = usernameField.getText().trim();
                admin.email = emailField.getText().trim();
                saved = true;
                dispose();
            });
            buttonPanel.add(saveBtn);

            add(buttonPanel, BorderLayout.SOUTH);
            this.admin = admin;
        }

        private JLabel styledLabel(String text, Font font) {
            JLabel label = new JLabel(text);
            label.setFont(font);
            label.setForeground(new Color(140,180,255));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            return label;
        }

        private JTextField styledField(String value, Font font) {
            JTextField field = new JTextField(value);
            field.setFont(font);
            field.setBackground(new Color(44,44,52));
            field.setForeground(Color.WHITE);
            field.setCaretColor(new Color(33,99,186));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60,80,120), 1, true),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            field.setMaximumSize(new Dimension(280, 38));
            return field;
        }

        public boolean isSaved() { return saved; }
        public Admin getAdmin() { return admin; }
    }
}