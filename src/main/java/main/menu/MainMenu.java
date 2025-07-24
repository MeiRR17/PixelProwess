package main.menu;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class MainMenu extends JPanel implements MouseListener {
    private boolean debugMode = false;


    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameState gameState;
    private BufferedImage menuImage;
    private final Polygon startButton, optionsButton, leaderboardButton, quitButton;
    private final int screenWidth, screenHeight;
    private double scaleX, scaleY;
    private BufferedImage backgroundImage;
    private final int MENU_WIDTH = 900;
    private final int MENU_HEIGHT = 700;
    private JLabel playerNameLabel;
    private BufferedImage loginImage;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private Font customFont;
    private final Color BUTTON_COLOR = new Color(54, 238, 224);
    private final Color TRANSLUCENT_TEXT = new Color(128, 128, 128, 128);
    private Timer loginSuccessTimer;
    private boolean showLoginUI = true;

    public MainMenu(GameState gameState, CardLayout cardLayout, JPanel cardPanel) {
        this.gameState = gameState;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        gameState.getSoundManager().startMenuMusic();

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight() - 30;

        // Initialize menu position and buttons
        int menuX = (screenWidth - MENU_WIDTH) / 2;
        int menuY = (screenHeight - MENU_HEIGHT) / 2;

        // Initialize button polygons
        startButton = createMenuButtonPolygon(menuX, menuY, new int[]{314, 321, 335, 563, 577, 585, 579, 567, 333, 320},
                new int[]{374, 358, 350, 350, 357, 373, 392, 399, 399, 391});

        optionsButton = createMenuButtonPolygon(menuX, menuY, new int[]{314, 321, 335, 563, 577, 585, 579, 567, 333, 320},
                new int[]{447, 431, 423, 423, 430, 446, 465, 472, 472, 464});

        leaderboardButton = createMenuButtonPolygon(menuX, menuY, new int[]{314, 321, 335, 563, 577, 585, 579, 567, 333, 320},
                new int[]{520, 504, 496, 496, 503, 519, 538, 545, 545, 537});

        quitButton = createMenuButtonPolygon(menuX, menuY, new int[]{314, 321, 335, 563, 577, 585, 579, 567, 333, 320},
                new int[]{593, 577, 569, 569, 576, 592, 611, 618, 618, 610});

        // Load custom font
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(getClass().getResourceAsStream("/font/LilitaOne-Regular.ttf")));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 16);
        }

        loadImages();
        setupPanel();
        setupLoginComponents();

        // Initialize player name label
        playerNameLabel = new JLabel();
        playerNameLabel.setForeground(Color.WHITE);
        playerNameLabel.setFont(customFont.deriveFont(16f));
        playerNameLabel.setBounds(20, 20, 200, 30);
        add(playerNameLabel);

        // Initialize success timer
        loginSuccessTimer = new Timer(500, e -> {
            showLoginUI = false;
            updatePlayerName();
            loginSuccessTimer.stop();
            repaint();
        });

        updatePlayerName();
    }

    private void loadLoginImage() {
        try {
            loginImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/menu/log in.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupLoginComponents() {
        setLayout(null);

        // Username field
        usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    g.setColor(TRANSLUCENT_TEXT);
                    g.drawString("Username", 5, 20);
                }
            }
        };
        usernameField.setBounds(120, 25, 280, 40);

        // Password field
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    g.setColor(TRANSLUCENT_TEXT);
                    g.drawString("Password", 5, 20);
                }
            }
        };
        passwordField.setBounds(120, 96, 280, 40);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBounds(440, 25, 130, 40);
        styleButton(loginButton);

        // Register button
        registerButton = new JButton("Sign Up");
        registerButton.setBounds(440, 96, 130, 40);
        styleButton(registerButton);

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setBounds(120, 150, 450, 30);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(customFont.deriveFont(14f));

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        // Add components
        add(usernameField);
        add(passwordField);
        add(loginButton);
        add(registerButton);
        add(messageLabel);
    }

    private void styleButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.BLACK);
        button.setFont(customFont.deriveFont(16f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (gameState.authenticate(username, password)) {
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Successfully logged in!");
            loginSuccessTimer.restart();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid username or password");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (gameState.register(username, password)) {
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Registration successful!");
            loginSuccessTimer.restart();
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Username already taken or registration failed");
        }
    }

    private void updatePlayerName() {
        if (gameState.isAuthenticated()) {
            playerNameLabel.setText("Logged in as: " + gameState.getPlayerName());
        } else {
            playerNameLabel.setText("");
        }
    }
    private Polygon createMenuButtonPolygon(int menuX, int menuY, int[] xPoints, int[] yPoints) {
        int[] adjustedX = new int[xPoints.length];
        int[] adjustedY = new int[yPoints.length];

        // Adjust coordinates relative to menu position
        for (int i = 0; i < xPoints.length; i++) {
            adjustedX[i] = menuX + xPoints[i];
            adjustedY[i] = menuY + yPoints[i];
        }

        return new Polygon(adjustedX, adjustedY, xPoints.length);
    }

    private void setupPanel() {
        setLayout(null);
        addMouseListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/opening/blured.JPG")));
            menuImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/menu/main.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Polygon createScaledPolygon(int[] xPoints, int[] yPoints) {
        int[] scaledX = new int[xPoints.length];
        int[] scaledY = new int[yPoints.length];

        for (int i = 0; i < xPoints.length; i++) {
            scaledX[i] = (int)(xPoints[i] * scaleX);
            scaledY[i] = (int)(yPoints[i] * scaleY);
        }

        return new Polygon(scaledX, scaledY, xPoints.length);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, null);
        }

        // Draw menu image in center
        if (menuImage != null) {
            int x = (screenWidth - MENU_WIDTH) / 2;
            int y = (screenHeight - MENU_HEIGHT) / 2;
            g.drawImage(menuImage, x, y, MENU_WIDTH, MENU_HEIGHT, null);
        }

        // Draw login UI only if not authenticated
        if (showLoginUI && !gameState.isAuthenticated()) {
            if (loginImage != null) {
                g.drawImage(loginImage, 0, 0, 600, 170, null);
            }
            usernameField.setVisible(true);
            passwordField.setVisible(true);
            loginButton.setVisible(true);
            registerButton.setVisible(true);
            messageLabel.setVisible(true);
        } else {
            usernameField.setVisible(false);
            passwordField.setVisible(false);
            loginButton.setVisible(false);
            registerButton.setVisible(false);
            messageLabel.setVisible(false);
        }

        // Debug visualization if enabled
        if (debugMode) {
            // ... existing debug drawing code ...
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        if (debugMode) {
            System.out.println("Click at: " + p.x + "," + p.y);
            System.out.println("Options button contains click: " + optionsButton.contains(p));
        }

        if (startButton.contains(p)) {
            gameState.initializePlayMenu(cardLayout, cardPanel);
            PlayMenu playMenu = gameState.getPlayMenu();
            cardPanel.add(playMenu, "PlayMenu");
            cardLayout.show(cardPanel, "PlayMenu");
            playMenu.requestFocus();
        } else if (optionsButton.contains(p)) {
            OptionsMenu optionsMenu = new OptionsMenu(cardLayout, cardPanel, gameState);
            cardPanel.add(optionsMenu, "OptionsMenu");
            cardLayout.show(cardPanel, "OptionsMenu");
            optionsMenu.requestFocus();
        } else if (leaderboardButton.contains(p)) {
            LeaderboardMenu leaderboardMenu = new LeaderboardMenu(cardLayout, cardPanel, gameState);
            cardPanel.add(leaderboardMenu, "LeaderboardMenu");
            cardLayout.show(cardPanel, "LeaderboardMenu");
            leaderboardMenu.requestFocus();
        } else if (quitButton.contains(p)) {
            System.exit(0);
        }
    }

    // Required interface methods
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}