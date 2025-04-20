package fishernet.ui;

import fishernet.dao.FishStockDAO;
import fishernet.model.Boat;
import fishernet.model.FishStock;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class FishStockForm extends JFrame {

    private JTextField tfSpecies, tfWeight;
    private Boat boat;

    public FishStockForm(Boat boat) {
        this.boat = boat;

        setTitle("Fish Stock for: " + boat.getRegistrationNo());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tfSpecies = new JTextField(20);
        tfWeight = new JTextField(20);
        JButton btnSave = new JButton("Save");

        btnSave.addActionListener(e -> save());

        setLayout(new GridLayout(5, 2, 10, 10));
        add(new JLabel("Boat:"));
        add(new JLabel(boat.getName()));
        add(new JLabel("Species:"));
        add(tfSpecies);
        add(new JLabel("Weight (kg):"));
        add(tfWeight);
        add(new JLabel("Date:"));
        add(new JLabel(LocalDate.now().toString()));
        add(new JLabel(""));
        add(btnSave);
    }

    private void save() {
        String species = tfSpecies.getText();
        String weightText = tfWeight.getText();

        if (species.isEmpty() || weightText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);

            FishStock stock = new FishStock(
                    boat.getId(),
                    species,
                    weight,
                    LocalDate.now().toString()
            );

            FishStockDAO.saveStock(stock);
            JOptionPane.showMessageDialog(this, "✅ Stock recorded successfully.");
            dispose(); // close the form after save
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Weight must be a number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error saving stock: " + e.getMessage());
        }
    }

    // Optional: for standalone testing (you must provide a mock Boat object)
    public static void main(String[] args) {
        Boat mockBoat = new Boat("TestBoat", "TEST123", "qr/TEST123.png");
        mockBoat.setId(1); // ensure this matches a real boat_id in your DB
        SwingUtilities.invokeLater(() -> new FishStockForm(mockBoat).setVisible(true));
    }
}
