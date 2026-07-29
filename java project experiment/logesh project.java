import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class CollabSphereGUI extends JFrame {

    private boolean isLogin = true;

    private final JTextField fullNameField = new JTextField();
    private final JTextField emailField = new JTextField("harish.narayanan@university.edu");
    private final JPasswordField passwordField = new JPasswordField("password123");
    private final JComboBox<String> roleBox =
            new JComboBox<>(new String[]{"Student", "Faculty"});

    private final JPanel fullNamePanel = new JPanel(new BorderLayout(5, 5));
    private final JLabel titleLabel = new JLabel("Welcome back", SwingConstants.CENTER);
    private final JButton submitButton = new JButton("Sign In");
    private final JButton switchButton = new JButton("Need an account? Create one");

    public CollabSphereGUI() {
        setTitle("CollabSphere");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color purple = new Color(53, 37, 205);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        mainPanel.setBackground(new Color(249, 249, 255));

        JLabel logo = new JLabel("CollabSphere", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setForeground(purple);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        headerPanel.setOpaque(false);
        headerPanel.add(logo);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 8, 8));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 196, 216)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        formPanel.add(new JLabel("Select Role"));
        formPanel.add(roleBox);

        fullNamePanel.setOpaque(false);
        fullNamePanel.add(new JLabel("Full Name"), BorderLayout.NORTH);
        fullNamePanel.add(fullNameField, BorderLayout.CENTER);
        fullNamePanel.setVisible(false);
        formPanel.add(fullNamePanel);

        formPanel.add(new JLabel("Email Address"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password"));

        JPanel passwordPanel = new JPanel(new BorderLayout(8, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JButton showButton = new JButton("Show");
        passwordPanel.add(showButton, BorderLayout.EAST);
        formPanel.add(passwordPanel);

        showButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('•');
                showButton.setText("Show");
            } else {
                passwordField.setEchoChar((char) 0);
                showButton.setText("Hide");
            }
        });

        submitButton.setBackground(purple);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submitForm());
        formPanel.add(submitButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        switchButton.setBorderPainted(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setForeground(purple);
        switchButton.setFocusPainted(false);
        switchButton.addActionListener(e -> switchMode());

        mainPanel.add(switchButton, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void switchMode() {
        isLogin = !isLogin;

        titleLabel.setText(isLogin ? "Welcome back" : "Create Account");
        submitButton.setText(isLogin ? "Sign In" : "Create Account");
        switchButton.setText(isLogin
                ? "Need an account? Create one"
                : "Already have an account? Sign in");

        fullNamePanel.setVisible(!isLogin);
        revalidate();
        repaint();
    }

    private void submitForm() {
        String role = (String) roleBox.getSelectedItem();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!isLogin && fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your full name.");
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password are required.");
            return;
        }

        String message = isLogin
                ? "Welcome back, " + role + "!"
                : "Account created successfully for " + role + "!";

        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CollabSphereGUI app = new CollabSphereGUI();
            app.setVisible(true);
        });
    }
}
