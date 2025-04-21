package dashboard;

import db.DBHelper;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class ReportsPanel extends JPanel {
    private JComboBox<String> reportSelect;
    private DatePicker fromDatePicker, toDatePicker;
    private JButton generateBtn;
    private JPanel contentPanel;
    private JTable reportTable;
    private JScrollPane scrollPane;

    private static final String[] REPORTS = {
            "New Boat Added Report",
            "New Stock Update Report",
            "Stocks Gain By Particular Boat Report",
            "New Admin Signup Report",
            "All Admin Changes Report"
    };

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 36, 48));
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout(16, 0));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33, 99, 186));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        topPanel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        filterPanel.setOpaque(false);

        reportSelect = new JComboBox<>(REPORTS);
        reportSelect.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reportSelect.setForeground(new Color(33,99,186));
        reportSelect.setBackground(Color.WHITE);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        fromLabel.setForeground(new Color(140,180,255));
        fromDatePicker = new DatePicker();
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        toLabel.setForeground(new Color(140,180,255));
        toDatePicker = new DatePicker();

        generateBtn = new JButton("Generate");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        generateBtn.setBackground(new Color(33, 99, 186));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);
        generateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generateBtn.setPreferredSize(new Dimension(120, 34));

        filterPanel.add(reportSelect);
        filterPanel.add(fromLabel);
        filterPanel.add(fromDatePicker);
        filterPanel.add(toLabel);
        filterPanel.add(toDatePicker);
        filterPanel.add(generateBtn);

        topPanel.add(filterPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        // Action
        generateBtn.addActionListener(e -> generateReport());

        // Set default date range to last month
        LocalDate today = LocalDate.now();
        fromDatePicker.setDate(today.minusMonths(1));
        toDatePicker.setDate(today);
    }

    private void generateReport() {
        int reportType = reportSelect.getSelectedIndex();
        LocalDate from = fromDatePicker.getDate();
        LocalDate to = toDatePicker.getDate();

        if (from == null || to == null) {
            showMessage("Please select both From and To dates.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (to.isBefore(from)) {
            showMessage("To date must be after or equal to From date.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (reportType) {
                case 0:
                    showTable(getNewBoatReport(from, to), new String[]{"Boat ID", "Name", "Registration No", "Owner", "Contact", "Capacity", "Added Date"});
                    break;
                case 1:
                    showTable(getNewStockUpdateReport(from, to), new String[]{"Stock ID", "Fish Type", "Boat", "Load (Kg)", "Date", "Added By"});
                    break;
                case 2:
                    showStocksGainByBoatReport(from, to);
                    break;
                case 3:
                    showTable(getNewAdminSignupReport(from, to), new String[]{"Admin ID", "Username", "Name", "Email", "Signup Date"});
                    break;
                case 4:
                    showTable(getAllAdminChangesReport(from, to), new String[]{"Change ID", "Admin", "Action", "Target Type", "Target Name/ID", "Change Time"});
                    break;
            }
        } catch (Exception ex) {
            showMessage("Failed to generate report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- REPORT 1: New Boat Added Report ---
    private Vector<Vector<Object>> getNewBoatReport(LocalDate from, LocalDate to) throws Exception {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT id, name, registration_number, owner_name, contact_no, capacity_Kg, created_at " +
                         "FROM boats WHERE created_at >= ? AND created_at <= ? ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("registration_number"));
                row.add(rs.getString("owner_name"));
                row.add(rs.getString("contact_no"));
                row.add(rs.getString("capacity_Kg"));
              //  row.add(rs.getString("created_at"));
                data.add(row);
            }
        }
        return data;
    }

    // --- REPORT 2: New Stock Update Report ---
    private Vector<Vector<Object>> getNewStockUpdateReport(LocalDate from, LocalDate to) throws Exception {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT s.id, s.fish_type, b.name AS boat, s.fish_load_kg, s.date, a.username AS added_by " +
                         "FROM fish_stocks s LEFT JOIN boats b ON s.boat_id=b.id " +
                         "LEFT JOIN admins a ON s.added_by=a.id " +
                         "WHERE s.date >= ? AND s.date <= ? ORDER BY s.date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("fish_type"));
                row.add(rs.getString("boat"));
                row.add(rs.getString("fish_load_kg"));
                row.add(rs.getString("date"));
                row.add(rs.getString("added_by"));
                data.add(row);
            }
        }
        return data;
    }

    // --- REPORT 3: Stocks Gain By Particular Boat Report ---
    private void showStocksGainByBoatReport(LocalDate from, LocalDate to) {
        // Ask for boat selection
        String boatName = selectBoatDialog();
        if (boatName == null) return;

        Vector<Vector<Object>> data = new Vector<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT s.id, s.fish_type, s.fish_load_kg, s.date, a.username AS added_by " +
                         "FROM fish_stocks s LEFT JOIN boats b ON s.boat_id=b.id " +
                         "LEFT JOIN admins a ON s.added_by=a.id " +
                         "WHERE b.name=? AND s.date >= ? AND s.date <= ? " +
                         "ORDER BY s.date DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, boatName);
            ps.setString(2, from.toString());
            ps.setString(3, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("fish_type"));
                row.add(rs.getString("fish_load_kg"));
                row.add(rs.getString("date"));
                row.add(rs.getString("added_by"));
                data.add(row);
            }
        } catch (Exception ex) {
            showMessage("DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showTable(data, new String[]{"Stock ID", "Fish Type", "Load (Kg)", "Date", "Added By"});
    }

    // --- REPORT 4: New Admin Signup Report ---
    private Vector<Vector<Object>> getNewAdminSignupReport(LocalDate from, LocalDate to) throws Exception {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT id, username, name, email, created_at FROM admins " +
                         "WHERE created_at >= ? AND created_at <= ? ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("created_at"));
                data.add(row);
            }
        }
        return data;
    }

    // --- REPORT 5: All Admin Changes Report ---
    private Vector<Vector<Object>> getAllAdminChangesReport(LocalDate from, LocalDate to) throws Exception {
        Vector<Vector<Object>> data = new Vector<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT c.id, a.username AS admin, c.action, c.target_type, c.target_name, c.change_time " +
                         "FROM admin_changes c LEFT JOIN admins a ON c.admin_id=a.id " +
                         "WHERE c.change_time >= ? AND c.change_time <= ? " +
                         "ORDER BY c.change_time DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("admin"));
                row.add(rs.getString("action"));
                row.add(rs.getString("target_type"));
                row.add(rs.getString("target_name"));
                row.add(rs.getString("change_time"));
                data.add(row);
            }
        }
        return data;
    }

    // --- BOAT SELECTION DIALOG FOR REPORT 3 ---
    private String selectBoatDialog() {
        Vector<String> boats = new Vector<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT name FROM boats ORDER BY name ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boats.add(rs.getString("name"));
            }
        } catch (Exception e) {
            showMessage("Failed to load boats: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (boats.isEmpty()) {
            showMessage("No boats found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        String selected = (String) JOptionPane.showInputDialog(
                this, "Select Boat:", "Choose Boat",
                JOptionPane.PLAIN_MESSAGE, null,
                boats.toArray(), boats.get(0)
        );
        return selected;
    }

    // --- SHOW TABLE ---
    private void showTable(Vector<Vector<Object>> data, String[] columns) {
        if (scrollPane != null) contentPanel.remove(scrollPane);
        Vector<String> colNames = new Vector<>();
        for (String c : columns) colNames.add(c);
        reportTable = new JTable(data, colNames) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        reportTable.setBackground(new Color(44, 44, 52));
        reportTable.setForeground(Color.WHITE);
        reportTable.setRowHeight(28);
        reportTable.getTableHeader().setBackground(new Color(33,99,186));
        reportTable.getTableHeader().setForeground(Color.WHITE);

        scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 4, 4, 4));
        scrollPane.setBackground(new Color(44, 44, 52));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- MESSAGE UTILS ---
    private void showMessage(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}