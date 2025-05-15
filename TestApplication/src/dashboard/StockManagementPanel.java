package dashboard;

import db.DBHelper;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * StockManagementPanel class - Panel for managing fish stock records.
 * Features:
 * - List, add, delete, and filter fish stocks by Boat ID, Date, and Fish Type.
 * - Uses LGoodDatePicker for date selection.
 * - QR code support for Boat ID.
 * - Animated messages for feedback.
 */
public class StockManagementPanel extends JPanel {
    // UI components
    private JPanel listPanel;
    private JTextField searchBoatField;
    private DatePicker searchDatePicker;
    private JComboBox<String> searchFishTypeCombo;
    private static final String[] FISH_TYPES = {"Tuna", "Salmon", "Prawns", "Crab", "Lobster", "Other"};
    private String adminName;

    /**
     * Constructor: Creates the StockManagementPanel UI.
     * @param adminName The name of the administrator (for audit purposes).
     */
    public StockManagementPanel(String adminName) {
        this.adminName = adminName;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 36, 48));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Top: Title + Add + Search/Filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Fish Stock Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.add(title, BorderLayout.WEST);

        // Add Stock Button
        JButton addBtn = styledButton("Add Fish Stock", new Color(33,99,186));
        addBtn.addActionListener(e -> showAddDialog());

        // Search/Filter Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);

        // Boat ID filter
        searchBoatField = styledField(10);
        searchBoatField.setToolTipText("Boat ID");

        // Date filter
        searchDatePicker = new DatePicker();
        searchDatePicker.setPreferredSize(new Dimension(140, 34));

        // Fish Type filter
        searchFishTypeCombo = new JComboBox<>();
        searchFishTypeCombo.addItem("All");
        for (String ft : FISH_TYPES) searchFishTypeCombo.addItem(ft);

        // Filter button
        JButton searchBtn = styledButton("Filter", new Color(33,99,186));
        searchBtn.addActionListener(e -> loadStocks());

        // Add filter controls to panel
        searchPanel.add(new JLabel("Boat ID:"));
        searchPanel.add(searchBoatField);
        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(searchDatePicker);
        searchPanel.add(new JLabel("Fish Type:"));
        searchPanel.add(searchFishTypeCombo);
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createHorizontalStrut(16));
        searchPanel.add(addBtn);

        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center: Stock List
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(30, 36, 48));
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setBackground(new Color(30, 36, 48));
        add(scroll, BorderLayout.CENTER);

        // Initial load
        loadStocks();
    }

    /**
     * Load and display fish stock records, applying filters if set.
     */
    private void loadStocks() {
        listPanel.removeAll();
        try (Connection con = DBHelper.getConnection()) {
            // Build SQL with optional filters
            StringBuilder sql = new StringBuilder(
                "SELECT s.boat_id, s.date, s.fish_type, s.fish_load_kg, b.name as boat_name " +
                "FROM fish_stocks s JOIN boats b ON s.boat_id = b.id"
            );
            ArrayList<String> params = new ArrayList<>();
            boolean hasWhere = false;

            String boatIdFilter = searchBoatField.getText().trim();
            LocalDate searchDate = searchDatePicker.getDate();
            String filterDate = (searchDate != null) ? searchDate.toString() : "";
            String fishTypeFilter = (String) searchFishTypeCombo.getSelectedItem();

            // Boat ID filter
            if (!boatIdFilter.isEmpty()) {
                sql.append(" WHERE s.boat_id = ?");
                params.add(boatIdFilter);
                hasWhere = true;
            }
            // Date filter
            if (filterDate != null && !filterDate.isEmpty()) {
                sql.append(hasWhere ? " AND " : " WHERE ");
                sql.append("s.date = ?");
                params.add(filterDate);
                hasWhere = true;
            }
            // Fish Type filter (not "All")
            if (fishTypeFilter != null && !"All".equals(fishTypeFilter)) {
                sql.append(hasWhere ? " AND " : " WHERE ");
                sql.append("s.fish_type = ?");
                params.add(fishTypeFilter);
            }

            sql.append(" ORDER BY s.date DESC, s.boat_id DESC");

            PreparedStatement ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); ++i) {
                ps.setString(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int boatId = rs.getInt("boat_id");
                String boatName = rs.getString("boat_name");
                String date = rs.getString("date");
                String fishType = rs.getString("fish_type");
                double fishLoad = rs.getDouble("fish_load_kg");
                listPanel.add(new StockRowPanel(boatId, boatName, date, fishType, fishLoad));
                listPanel.add(Box.createVerticalStrut(5));
            }
            if (!hasResults) {
                JLabel emptyLabel = new JLabel("No stocks found.");
                emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                emptyLabel.setForeground(new Color(130, 160, 210));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JPanel emptyPanel = new JPanel();
                emptyPanel.setOpaque(false);
                emptyPanel.add(emptyLabel);
                listPanel.add(emptyPanel);
            }
        } catch (Exception e) {
            JLabel errLabel = new JLabel("Failed to load stocks: " + e.getMessage());
            errLabel.setForeground(Color.RED);
            listPanel.add(errLabel);
        }
        revalidate();
        repaint();
    }

    /**
     * Row Panel for each Fish Stock record.
     */
    class StockRowPanel extends JPanel {
        Color normalBg = new Color(40, 50, 68);
        Color hoverBg = new Color(44, 63, 104);

        public StockRowPanel(int boatId, String boatName, String date, String fishType, double fishLoad) {
            setBackground(normalBg);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setLayout(new BorderLayout(18, 0));
            setMaximumSize(new Dimension(10000, 100));
            setPreferredSize(new Dimension(100, 100));

            // --- Left: Boat info ---
            JPanel leftPanel = new JPanel();
            leftPanel.setOpaque(false);
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            JPanel idNamePanel = new JPanel();
            idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
            idNamePanel.setOpaque(false);

            JLabel idLabel = new JLabel("Boat ID: " + boatId + "  ");
            idLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            idLabel.setForeground(Color.WHITE);
            idNamePanel.add(idLabel);

            JLabel nameLabel = new JLabel("Boat Name: " + boatName + " ");
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

            JLabel dateLabel = new JLabel("Date: " + date);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            dateLabel.setForeground(new Color(140, 180, 255));
            dateLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

            leftPanel.add(idNamePanel);
            leftPanel.add(nameLabel);
            leftPanel.add(dateLabel);

            add(leftPanel, BorderLayout.WEST);

            // --- Center: Fish Type & Load ---
            JPanel centerPanel = new JPanel();
            centerPanel.setOpaque(false);
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            JLabel fishTypeLabel = new JLabel("Fish Type" + fishType + "  ");
            fishTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            fishTypeLabel.setForeground(new Color(140, 180, 255));
            JLabel fishLoadLabel = new JLabel("Load: " + fishLoad + " Kg");
            fishLoadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            fishLoadLabel.setForeground(new Color(200, 245, 200));
            centerPanel.add(fishTypeLabel);
            centerPanel.add(fishLoadLabel);

            add(centerPanel, BorderLayout.CENTER);

            // --- Right: Delete Button ---
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 22));
            btnPanel.setOpaque(false);
            JButton deleteBtn = styledButton("Delete", new Color(220, 80, 80));
            btnPanel.add(deleteBtn);
            add(btnPanel, BorderLayout.EAST);

            // Delete action
            deleteBtn.addActionListener(e -> deleteStock(boatId, date, fishType));

            // Hover effect
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    setBackground(hoverBg);
                }
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    setBackground(normalBg);
                }
            });
        }

        /**
         * Helper: Styled button.
         */
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

        /**
         * Deletes a stock record after confirming with the user.
         */
        private void deleteStock(int boatId, String date, String fishType) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this fish stock record?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection con = DBHelper.getConnection()) {
                    String sql = "DELETE FROM fish_stocks WHERE boat_id=? AND date=? AND fish_type=?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, boatId);
                    ps.setString(2, date);
                    ps.setString(3, fishType);
                    ps.executeUpdate();
                    AnimatedMessage.showMessage(this, "Fish stock deleted.", "Success", AnimatedMessage.Type.SUCCESS);
                    StockManagementPanel.this.loadStocks();
                } catch (Exception ex) {
                    AnimatedMessage.showMessage(this, "Delete failed: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
                }
            }
        }
    }

    /**
     * Show dialog for adding a new fish stock record.
     */
    private void showAddDialog() {
        AddStockDialog dialog = new AddStockDialog();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (dialog.isAdded()) {
            loadStocks();
        }
    }

    /**
     * Dialog for adding a new fish stock entry.
     */
    public class AddStockDialog extends JDialog {
        private boolean added = false;
        private DatePicker datePicker;
        private JTextField fishLoadField;
        private JComboBox<Integer> boatIdCombo;
        private JComboBox<String> fishTypeCombo;

        public AddStockDialog() {
            super((Frame) null, "Add Fish Stock", true);
            setLayout(new BorderLayout());
            setBackground(new Color(40, 50, 68));
            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(new Color(40, 50, 68));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(10, 10, 10, 10);
            c.anchor = GridBagConstraints.WEST;

            // Date field
            c.gridx = 0; c.gridy = 0; form.add(styledLabel("Date (YYYY-MM-DD):"), c);
            c.gridx = 1; 
            datePicker = new DatePicker();
            form.add(datePicker, c);

            // QR Scan button for Boat ID
            JButton scanQRBtn = styledButton("Scan QR", new Color(33, 186, 99));
            scanQRBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

            // Boat ID dropdown
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

            // Fish type dropdown
            c.gridx = 0; c.gridy = 2; form.add(styledLabel("Fish Type:"), c);
            c.gridx = 1; fishTypeCombo = new JComboBox<>(FISH_TYPES); form.add(fishTypeCombo, c);

            // Fish load field
            c.gridx = 0; c.gridy = 3; form.add(styledLabel("Fish Load (Kg):"), c);
            c.gridx = 1; fishLoadField = styledField(18); form.add(fishLoadField, c);

            // Add/Cancel buttons
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
            btnPanel.setOpaque(false);
            JButton addBtn = styledButton("Add", new Color(33,99,186));
            JButton cancelBtn = styledButton("Cancel", new Color(120,120,120));
            btnPanel.add(addBtn); btnPanel.add(cancelBtn);

            add(form, BorderLayout.CENTER);
            add(btnPanel, BorderLayout.SOUTH);

            pack();
            setResizable(false);

            // Cancel action
            cancelBtn.addActionListener(e -> dispose());

            // Add action: validate, insert into DB
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

        /**
         * Select boatId if present, else add it (for QR scan).
         */
        private void selectOrAddBoatId(int boatId) {
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

        /**
         * Get all Boat IDs and names from the database.
         */
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

        /**
         * Helper: styled label for the form.
         */
        private JLabel styledLabel(String text) {
            JLabel l = new JLabel(text);
            l.setForeground(new Color(140,180,255));
            l.setFont(new Font("Segoe UI", Font.BOLD, 15));
            return l;
        }

        /**
         * Helper: styled text field for the form.
         */
        private JTextField styledField(int columns) {
            JTextField field = new JTextField(columns);
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

        /**
         * Helper: styled button for the form.
         */
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

        /** @return true if stock was added. */
        public boolean isAdded() { return added; }
    }

    // --- Helper: styled button for top panel and filter ---
    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // --- Helper: styled text field for filter ---
    private JTextField styledField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(new Color(44, 44, 52));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(33,99,186));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 120), 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return field;
    }
}