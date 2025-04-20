/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.ui;



import fishernet.dao.UserDAO;
 import javax.swing.*;
 import java.awt.*;
 public class Login extends JFrame {
    public Login() {
        setTitle("FisherNet Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // UI Components
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            boolean success = UserDAO.login(user, pass);
            if (success) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new Dashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });
        // Layout
        setLayout(new GridLayout(4, 1));
        add(userLabel); add(userField);
        add(passLabel); add(passField);
        add(loginButton);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
 }