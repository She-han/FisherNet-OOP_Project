package dashboard;

import db.DBHelper;
import util.QRCodeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.mindrot.jbcrypt.BCrypt;

// ---- Main Frame ----
public class HomePageFrame extends JFrame {
    private CardLayout rightCardLayout;    
    private JPanel rightPanel;
    private FisherNetLogoPanel logoPanel;

    public HomePageFrame() {
        setTitle("FisherNet - Version 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(24, 32, 44));

        // Left: Logo and theme
        logoPanel = new FisherNetLogoPanel();
        logoPanel.setPreferredSize(new Dimension(900, 0));
        mainPanel.add(logoPanel, BorderLayout.WEST);

        // Right: CardLayout for switching between login/signup
        rightCardLayout = new CardLayout();
        rightPanel = new JPanel(rightCardLayout);
        rightPanel.setBackground(new Color(34,45,65));
        rightPanel.add(new LoginPanel(rightPanel, rightCardLayout), "login");
        rightPanel.add(new SignUpPanel(rightPanel, rightCardLayout), "signup");

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    // Custom logo and theme panel (edit as needed for your branding)
    static class FisherNetLogoPanel extends JPanel {
        private BufferedImage logoImage;

        public FisherNetLogoPanel() {
            setBackground(new Color(24, 32, 44));
            try {
                // Replace "path/to/logo.png" with the actual path to your logo image
                logoImage = ImageIO.read(getClass().getResourceAsStream("/icons/logofully.png"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load logo image.");
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Draw wave theme background
            int w = getWidth(), h = getHeight();
            g2d.setPaint(new GradientPaint(0, 0, new Color(33,99,186), w, h, new Color(24,32,44)));
            g2d.fillRect(0, 0, w, h);
            
            if (logoImage != null) {
                int logoWidth = 625;
                int logoHeight = 443;
                int logoX = 30;
                int logoY = h / 2 - 200;
                g2d.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, this);
            }
        }
    }

    // LOGIN PANEL
    class LoginPanel extends JPanel {
        private PlaceholderTextField usernameField;
        private PlaceholderPasswordField passwordField;
        private JPanel rightPanel;
        private CardLayout rightCardLayout;

        public LoginPanel(JPanel rightPanel, CardLayout rightCardLayout) {
            this.rightPanel = rightPanel;
            this.rightCardLayout = rightCardLayout;

            setBackground(new Color(34, 45, 65));
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 12, 8, 12);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.weightx = 1;

            JPanel form = new JPanel();
            form.setBackground(new Color(34, 45, 65));
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0)); // Padding

            // Title
            JLabel title = new JLabel("Admin Login");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(new Color(33, 99, 186));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(title);
            form.add(Box.createVerticalStrut(32));

            // Username field
            usernameField = new PlaceholderTextField("Username");
            usernameField.setMaximumSize(new Dimension(250, 36));
            usernameField.setPreferredSize(new Dimension(250, 36));
            usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(usernameField);
            form.add(Box.createVerticalStrut(16));

            // Password field
            passwordField = new PlaceholderPasswordField("Password");
            passwordField.setMaximumSize(new Dimension(250, 36));
            passwordField.setPreferredSize(new Dimension(250, 36));
            passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(passwordField);
            form.add(Box.createVerticalStrut(20));

            // Login button
            JButton loginBtn = styledButton("Login", new Color(33, 99, 186));
            loginBtn.setMaximumSize(new Dimension(250, 38));
            loginBtn.setPreferredSize(new Dimension(250, 38));
            loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginBtn.addActionListener(e -> doLogin());
            form.add(loginBtn);
            form.add(Box.createVerticalStrut(18));

            // Sign up link
            JLabel link = new JLabel("Don't have an account? Sign Up");
            link.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            link.setForeground(new Color(140, 180, 255));
            link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            link.setAlignmentX(Component.CENTER_ALIGNMENT);
            link.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (rightCardLayout != null && rightPanel != null) {
                        rightCardLayout.show(rightPanel, "signup");
                    }
                }
            });
            form.add(link);

            add(form, gbc);

            // Set initial focus to username field
            SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
        }

        private JButton styledButton(String text, Color bg) {
            JButton btn = new JButton(text);
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(9, 24, 9, 24));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
                public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
            });
            return btn;
        }

        // --- SECURE LOGIN USING HASHED PASSWORD ---
        private void doLogin() {
            String uname = usernameField.getText().trim();
            String pwd = new String(passwordField.getPassword());
            if (uname.isEmpty() || pwd.isEmpty()) {
                AnimatedMessage.showMessage(this, "Fill all fields!", "Warning", AnimatedMessage.Type.WARNING);
                return;
            }
            try (Connection con = DBHelper.getConnection()) {
                // Only fetch by username, then compare hash in Java!
                String sql = "SELECT first_name, last_name, username, email, password FROM admins WHERE username=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, uname);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String hashedPwd = rs.getString("password");
                    if (BCrypt.checkpw(pwd, hashedPwd)) {
                        AnimatedMessage.showMessage(this, "Login success", "Success", AnimatedMessage.Type.SUCCESS);
                        Admin admin = new Admin(
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("username"),
                                rs.getString("email"),
                                hashedPwd // Do not store plain password
                        );
                        SwingUtilities.getWindowAncestor(this).dispose();
                        new MainFrame(admin);
                    } else {
                        AnimatedMessage.showMessage(this, "Login failed! Incorrect username or password", "Error", AnimatedMessage.Type.ERROR);
                    }
                } else {
                    AnimatedMessage.showMessage(this, "Login failed! Incorrect username or password", "Error", AnimatedMessage.Type.ERROR);
                }
            } catch (Exception ex) {
                AnimatedMessage.showMessage(this, "Login error: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
            }
        }
    }

    // SIGNUP PANEL
    class SignUpPanel extends JPanel {
        private PlaceholderTextField firstNameField, lastNameField, usernameField, emailField;
        private PlaceholderPasswordField passwordField;
        private JPanel rightPanel;
        private CardLayout rightCardLayout;

        public SignUpPanel(JPanel rightPanel, CardLayout rightCardLayout) {
            this.rightPanel = rightPanel;
            this.rightCardLayout = rightCardLayout;

            setBackground(new Color(34,45,65));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 12, 8, 12);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.weightx = 1;

            JPanel form = new JPanel();
            form.setBackground(new Color(34,45,65));
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

            JLabel title = new JLabel("Admin Sign Up");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(new Color(33,99,186));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(title);
            form.add(Box.createVerticalStrut(24));

            firstNameField = new PlaceholderTextField("First Name");
            firstNameField.setMaximumSize(new Dimension(250, 36));
            firstNameField.setPreferredSize(new Dimension(250, 36));
            firstNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(firstNameField);
            form.add(Box.createVerticalStrut(8));

            lastNameField = new PlaceholderTextField("Last Name");
            lastNameField.setMaximumSize(new Dimension(250, 36));
            lastNameField.setPreferredSize(new Dimension(250, 36));
            lastNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(lastNameField);
            form.add(Box.createVerticalStrut(8));

            usernameField = new PlaceholderTextField("Username");
            usernameField.setMaximumSize(new Dimension(250, 36));
            usernameField.setPreferredSize(new Dimension(250, 36));
            usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(usernameField);
            form.add(Box.createVerticalStrut(8));

            passwordField = new PlaceholderPasswordField("Password");
            passwordField.setMaximumSize(new Dimension(250, 36));
            passwordField.setPreferredSize(new Dimension(250, 36));
            passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(passwordField);
            form.add(Box.createVerticalStrut(8));

            emailField = new PlaceholderTextField("Mail Address");
            emailField.setMaximumSize(new Dimension(250, 36));
            emailField.setPreferredSize(new Dimension(250, 36));
            emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
            form.add(emailField);
            form.add(Box.createVerticalStrut(14));

            JButton signUpBtn = styledButton("Sign Up", new Color(33,99,186));
            signUpBtn.setMaximumSize(new Dimension(250, 38));
            signUpBtn.setPreferredSize(new Dimension(250, 38));
            signUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            signUpBtn.addActionListener(e -> doSignUp());
            form.add(signUpBtn);
            form.add(Box.createVerticalStrut(18));

            JLabel link = new JLabel("Already have an account? Login");
            link.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            link.setForeground(new Color(140,180,255));
            link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            link.setAlignmentX(Component.CENTER_ALIGNMENT);
            link.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (rightCardLayout != null && rightPanel != null) {
                        rightCardLayout.show(rightPanel, "login");
                    }
                }
            });
            form.add(link);

            add(form, gbc);
        }

        // --- SECURE SIGNUP: HASH PASSWORD BEFORE STORING ---
        private void doSignUp() {
            String fname = firstNameField.getText().trim();
            String lname = lastNameField.getText().trim();
            String uname = usernameField.getText().trim();
            String mail = emailField.getText().trim();
            String pwd = new String(passwordField.getPassword());

            if(fname.isEmpty() || lname.isEmpty() || uname.isEmpty() || pwd.isEmpty() || mail.isEmpty()) {
                AnimatedMessage.showMessage(this, "Fill all fields!", "Warning", AnimatedMessage.Type.WARNING);
                return;
            }
            try (Connection con = DBHelper.getConnection()) {
                String checkSql = "SELECT id FROM admins WHERE username=?";
                PreparedStatement checkPs = con.prepareStatement(checkSql);
                checkPs.setString(1, uname);
                ResultSet rs = checkPs.executeQuery();
                if(rs.next()) {
                    AnimatedMessage.showMessage(this, "Username already exists!", "Warning", AnimatedMessage.Type.WARNING);
                    return;
                }

                // Hash the password before storing
                String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());
                String sql = "INSERT INTO admins (first_name, last_name, username, password, email) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, fname);
                ps.setString(2, lname);
                ps.setString(3, uname);
                ps.setString(4, hashed);
                ps.setString(5, mail);
                ps.executeUpdate();

                AnimatedMessage.showMessage(this, "Sign up success! Please login.", "Success", AnimatedMessage.Type.SUCCESS);
                if (rightCardLayout != null && rightPanel != null) {
                    rightCardLayout.show(rightPanel, "login");
                }
            } catch (Exception ex) {
                AnimatedMessage.showMessage(this, "Sign up error: " + ex.getMessage(), "Error", AnimatedMessage.Type.ERROR);
            }
        }
    }

    // --- Styled components ---
    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(140,180,255));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    private JTextField styledField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(44, 44, 52));
        field.setForeground(Color.WHITE);
        field.setCaretColor(new Color(33,99,186));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 80, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(280, 38));
    }
    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    // --- Main entry point ---
}