package game.entity.ui;

import main.data.FirebaseManager;
import main.menu.GameState;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JPanel {
    private FirebaseManager firebaseManager;
    private boolean loggedIn = false;

    public LoginUI(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (GameState.getInstance().authenticate(username, password)) {
                loggedIn = true;
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Login failed. Please try again.");
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (GameState.getInstance().register(username, password)) {
                loggedIn = true;
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Registration failed. Please try again.");
            }
        });

        // Layout adjustments
        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
