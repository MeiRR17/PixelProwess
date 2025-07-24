package main.menu;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Objects;
import java.net.URL;

public class OpeningScreen extends JPanel implements KeyListener, MouseListener {
    private boolean isVideoReady = false;
    private boolean isTransitioning = false;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameState gameState;
    private boolean debugMode = false;

    // Images
    private BufferedImage openingImage;
    private BufferedImage intermediateImage;
    private Icon logoGif;
    private BufferedImage blurredBackground;

    // Transition animation
    private float fadeAlpha = 0.0f;
    private Timer fadeTimer;
    private BufferedImage currentBuffer;
    private BufferedImage nextBuffer;

    // State management
    private enum ScreenState {
        OPENING,
        INTERMEDIATE,
        VIDEO,
        MAIN_MENU
    }
    private ScreenState currentState = ScreenState.OPENING;

    // GIF positioning and size
    private int gifX, gifY, gifWidth, gifHeight;

    // Screen dimensions
    private final int screenWidth, screenHeight;
    private double scaleX, scaleY;

    // Video components
    private JFXPanel videoPanel;
    private MediaPlayer mediaPlayer;

    public OpeningScreen(GameState gameState, CardLayout cardLayout, JPanel cardPanel) {
        this.gameState = gameState;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        new JFXPanel();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = (int) screenSize.getWidth();
        this.screenHeight = (int) screenSize.getHeight() - 30;

        setLayout(null);
        setupGifDimensions();
        loadImages();
        setupListeners();
        initializeVideoPanel();
        initializeBuffers();

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        initializeVideoPanel();
        prepareVideo();
    }
    private void prepareVideo() {
        javafx.application.Platform.runLater(() -> {
            try {
                URL mediaUrl = getClass().getResource("/game set/opening/from opening to main menu.mp4");
                if (mediaUrl != null) {
                    Media media = new Media(mediaUrl.toString());
                    mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);

                    mediaView.setFitWidth(screenWidth);
                    mediaView.setFitHeight(screenHeight);

                    final javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
                    root.setStyle("-fx-background-color: transparent;");
                    root.getChildren().add(mediaView);

                    javafx.scene.Scene scene = new javafx.scene.Scene(root);
                    scene.setFill(null);
                    videoPanel.setScene(scene);

                    mediaPlayer.setOnEndOfMedia(this::showMainMenu);

                    mediaPlayer.statusProperty().addListener((observable, oldStatus, newStatus) -> {
                        if (newStatus == MediaPlayer.Status.PLAYING) {
                            root.setStyle("-fx-background-color: black;");

                            SwingUtilities.invokeLater(() -> {
                                currentState = ScreenState.VIDEO;
                                videoPanel.setVisible(true);
                                repaint();
                            });
                        }
                    });

                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    mediaPlayer.pause();
                    isVideoReady = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void setupGifDimensions() {
        // Original image dimensions
        final int ORIGINAL_WIDTH = 8000;
        final int ORIGINAL_HEIGHT = 4500;

        // Original GIF coordinates in the 8000x4500 space
        final int ORIGINAL_GIF_X = 5800;
        final int ORIGINAL_GIF_Y = 240;
        final int ORIGINAL_GIF_WIDTH = 2400; // 8200 - 5800
        final int ORIGINAL_GIF_HEIGHT = 1000;

        // Calculate scaling factors
        scaleX = (double) screenWidth / ORIGINAL_WIDTH;
        scaleY = (double) screenHeight / ORIGINAL_HEIGHT;

        // Scale the coordinates and dimensions to match screen size
        gifX = (int) (ORIGINAL_GIF_X * scaleX);
        gifY = (int) (ORIGINAL_GIF_Y * scaleY);
        gifWidth = (int) (ORIGINAL_GIF_WIDTH * scaleX);
        gifHeight = (int) (ORIGINAL_GIF_HEIGHT * scaleY);

        // Debug output
        System.out.println("Screen dimensions: " + screenWidth + "x" + screenHeight);
        System.out.println("Scaled GIF position: " + gifX + "," + gifY);
        System.out.println("Scaled GIF size: " + gifWidth + "x" + gifHeight);
    }

    private void initializeBuffers() {
        currentBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        nextBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

        // Initialize the opening screen in the current buffer
        Graphics2D g = currentBuffer.createGraphics();
        g.drawImage(openingImage, 0, 0, screenWidth, screenHeight, null);
        if (logoGif != null) {
            logoGif.paintIcon(this, g, gifX, gifY);
        }
        g.dispose();
    }
    private void loadImages() {
        try {
            // Load and verify opening image
            openingImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/opening/main.png")));
            System.out.println("Opening image loaded: " + (openingImage != null));

            // Load and verify intermediate image
            intermediateImage = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/opening/1.jpg")));
            System.out.println("Intermediate image loaded: " + (intermediateImage != null));

            // Load and verify blurred background
            blurredBackground = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game set/opening/blured.JPG")));
            System.out.println("Blurred background loaded: " + (blurredBackground != null));

            // Load GIF
            URL gifUrl = getClass().getResource("/game set/extension/logo.gif");
            if (gifUrl != null) {
                ImageIcon tempIcon = new ImageIcon(gifUrl);

                // Wait for the image to load completely
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(tempIcon.getImage(), 0);
                try {
                    tracker.waitForAll();
                } catch (InterruptedException e) {
                    System.err.println("Image loading interrupted: " + e.getMessage());
                }

                // Verify the image loaded successfully
                if (tracker.isErrorAny()) {
                    System.err.println("Error loading GIF image");
                    return;
                }

                // Get the original dimensions
                int originalWidth = tempIcon.getIconWidth();
                int originalHeight = tempIcon.getIconHeight();

                if (originalWidth <= 0 || originalHeight <= 0) {
                    System.err.println("Invalid GIF dimensions: " + originalWidth + "x" + originalHeight);
                    return;
                }

                // Scale the image
                Image scaledImage = tempIcon.getImage().getScaledInstance(
                        gifWidth,
                        gifHeight,
                        Image.SCALE_SMOOTH
                );

                // Create the final icon
                logoGif = new ImageIcon(scaledImage);

                // Verify the scaled image
                System.out.println("Original GIF size: " + originalWidth + "x" + originalHeight);
                System.out.println("Scaled GIF size: " + logoGif.getIconWidth() + "x" + logoGif.getIconHeight());
            } else {
                System.err.println("Could not find GIF resource at: /game set/extension/logo.gif");
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void startTransition() {
        if (currentState == ScreenState.OPENING && !isTransitioning) {
            isTransitioning = true;

            if (isVideoReady) {
                System.out.println("Transitioning to video...");

                currentState = ScreenState.VIDEO;

                videoPanel.setVisible(true);

                javafx.application.Platform.runLater(() -> {
                    mediaPlayer.play();
                });

                repaint();
            } else {
                System.err.println("ההvideo not ready skipping to main menu.");
                showMainMenu();
            }
        }
    }

    private void playTransitionVideo() {
        System.out.println("Starting video playbackk");
        videoPanel.setVisible(true);
        repaint();

        try {
            URL mediaUrl = getClass().getResource("/game set/opening/from opening to main menu.mp4");
            if (mediaUrl == null) {
                System.err.println("Could not find transition video");
                showMainMenu();
                return;
            }

            javafx.application.Platform.runLater(() -> {
                try {
                    Media media = new Media(mediaUrl.toString());
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }

                    mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);

                    mediaView.setFitWidth(screenWidth);
                    mediaView.setFitHeight(screenHeight);

                    javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
                    root.setStyle("-fx-background-color: black;");
                    root.getChildren().add(mediaView);

                    javafx.scene.Scene scene = new javafx.scene.Scene(root);
                    videoPanel.setScene(scene);

                    mediaPlayer.setOnEndOfMedia(this::showMainMenu);
                    mediaPlayer.play();
                    System.out.println("Video playback started");
                } catch (Exception e) {
                    System.err.println("Error during video playback: " + e.getMessage());
                    e.printStackTrace();
                    showMainMenu();
                }
            });
        } catch (Exception e) {
            System.err.println("Error setting up video: " + e.getMessage());
            e.printStackTrace();
            showMainMenu();
        }
    }

    private void showMainMenu() {
        javafx.application.Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
        });

        currentState = ScreenState.MAIN_MENU;
        isTransitioning = false;
        videoPanel.setVisible(false);

        MainMenu mainMenu = new MainMenu(gameState, cardLayout, cardPanel);
        cardPanel.add(mainMenu, "MainMenu");
        cardLayout.show(cardPanel, "MainMenu");
        mainMenu.requestFocus();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (currentState == ScreenState.OPENING) {
            if (openingImage != null) {
                g2d.drawImage(openingImage, 0, 0, screenWidth, screenHeight, null);
            }
            if (logoGif != null) {
                logoGif.paintIcon(this, g2d, gifX, gifY);
            }
        }

    }

    public void setGifBounds(int x, int y, int width, int height) {
        this.gifX = x;
        this.gifY = y;
        this.gifWidth = width;
        this.gifHeight = height;

        if (logoGif != null && logoGif instanceof ImageIcon) {
            ImageIcon originalIcon = (ImageIcon) logoGif;
            logoGif = new ImageIcon(
                    originalIcon.getImage().getScaledInstance(
                            width,
                            height,
                            Image.SCALE_SMOOTH
                    )
            );
        }
        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        startTransition();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startTransition();
    }

    private void initializeVideoPanel() {
        videoPanel = new JFXPanel();
        videoPanel.setBounds(0, 0, screenWidth, screenHeight);
        videoPanel.setVisible(false);
        videoPanel.setOpaque(false);
        videoPanel.setBackground(new Color(0, 0, 0, 0));
        add(videoPanel);
    }

    private void setupListeners() {
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
    }



    // Required interface methods
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}