package game.object.weapon;

import main.menu.GamePanel;
import main.menu.OptionsMenu;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public abstract class Weapon implements Cloneable {
    public static final Random random = new Random();
    protected String gunImagePath;  // Image used in-game
    protected String[] iconImagePath; // Image used as tile icon
    protected String bulletPath;
    public BufferedImage gunImage;
    public double scale;
    public BufferedImage iconImage;
    public BufferedImage bulletImage;
    public String weaponName;
    public boolean collision;
    public int worldX, worldY;
    public Rectangle bounds = new Rectangle(0 ,0 ,32 , 32);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    protected String shootSoundPath;
    protected String reloadSoundPath;
    public boolean isAutomatic;
    protected Clip shootSound;
    protected Clip reloadSound;
    private boolean isShooting;

    public String rarity;
    private boolean soundPlaying = false;

    // Weapon states
    public int DAMAGE;
    public int MAGAZINE_SIZE;
    public double FIRE_RATE;
    public double RELOAD_TIME;

    public int ammoLeft;
    public boolean isReloading;
    protected long reloadStartTime;

    protected FloatControl shootSoundVolume;
    protected FloatControl reloadSoundVolume;
    protected double originalReloadLength;
    protected long reloadSoundLength;

    public Weapon(String gunImagePath, double scale, String[] iconImagePath, String weaponName,
                  String bulletPath, int DAMAGE, int MAGAZINE_SIZE, double FIRE_RATE,
                  double RELOAD_TIME, boolean isAutomatic) throws IOException {
        this.gunImagePath = gunImagePath;
        this.scale = scale;
        this.iconImagePath = iconImagePath;
        this.weaponName = weaponName;
        this.bulletPath = bulletPath;

        // Load images using the new loadImage method
        this.gunImage = loadImage(this.gunImagePath); // Load gun image first
        this.gunImage = resizeImage(this.gunImage, (int) this.scale * gunImage.getWidth(), (int) this.scale * gunImage.getHeight());
        this.iconImage = resizeImage(loadImage(iconImagePath[getRarity()]));
        this.bulletImage = loadImage(this.bulletPath);

        this.DAMAGE = DAMAGE;
        this.MAGAZINE_SIZE = MAGAZINE_SIZE;
        this.ammoLeft = MAGAZINE_SIZE;
        this.FIRE_RATE = FIRE_RATE;
        this.RELOAD_TIME = RELOAD_TIME;

        // Adjust stats based on rarity
        adjustStatsBasedOnRarity();
        this.isReloading = false;
        this.isAutomatic = isAutomatic;
        this.shootSoundPath = "audio/sound effect/weapons/" + weaponName + "/" +
                (isAutomatic ? "shoot(auto).wav" : "shoot.wav");
        this.reloadSoundPath = "audio/sound effect/weapons/" + weaponName + "/reload.wav";

        try {
            initializeSounds();
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public void playShootSound() {
        // Check if audio is enabled through OptionsMenu
        if (!OptionsMenu.isAudioEnabled() || shootSound == null) return;

        if (isAutomatic) {
            if (!isShooting) {
                shootSound.setFramePosition(0);
                shootSound.loop(Clip.LOOP_CONTINUOUSLY);
                isShooting = true;
            }
        } else {
            shootSound.stop();
            shootSound.setFramePosition(0);
            shootSound.start();
        }
    }

    public void stopShootSound() {
        if (shootSound != null && isShooting) {
            shootSound.stop();
            shootSound.setFramePosition(0);
            isShooting = false;
        }
    }

    public void playReloadSound() {
        if (!OptionsMenu.isAudioEnabled() || reloadSound == null) return;

        reloadSound.stop();
        reloadSound.setFramePosition(0);
        // Adjust reload sound speed based on reload time
        double speedFactor = originalReloadLength / RELOAD_TIME;
        long newLength = (long)(reloadSoundLength / speedFactor);

        // Create a new audio input stream with adjusted speed
        try {
            AudioInputStream original = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getClassLoader().getResource(reloadSoundPath))
            );
            AudioFormat format = original.getFormat();
            AudioFormat newFormat = new AudioFormat(
                    format.getEncoding(),
                    format.getSampleRate() * (float)speedFactor,
                    format.getSampleSizeInBits(),
                    format.getChannels(),
                    format.getFrameSize(),
                    format.getFrameRate() * (float)speedFactor,
                    format.isBigEndian()
            );

            AudioInputStream adjustedStream = AudioSystem.getAudioInputStream(newFormat, original);
            reloadSound.close();
            reloadSound.open(adjustedStream);
            reloadSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initializeSounds() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (getClass().getClassLoader().getResource(shootSoundPath) == null) {
            throw new IOException("Shoot sound file not found: " + shootSoundPath);
        }
        if (getClass().getClassLoader().getResource(reloadSoundPath) == null) {
            throw new IOException("Reload sound file not found: " + reloadSoundPath);
        }

        // Load shoot sound
        AudioInputStream shootStream = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(getClass().getClassLoader().getResource(shootSoundPath))
        );
        shootSound = AudioSystem.getClip();
        shootSound.open(shootStream);
        shootSoundVolume = (FloatControl) shootSound.getControl(FloatControl.Type.MASTER_GAIN);

        // Load reload sound and store its original length
        AudioInputStream reloadStream = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(getClass().getClassLoader().getResource(reloadSoundPath))
        );
        reloadSound = AudioSystem.getClip();
        reloadSound.open(reloadStream);
        reloadSoundVolume = (FloatControl) reloadSound.getControl(FloatControl.Type.MASTER_GAIN);
        reloadSoundLength = reloadSound.getMicrosecondLength();
        originalReloadLength = RELOAD_TIME;
    }


    public Weapon clone() throws CloneNotSupportedException {
        Weapon cloned = (Weapon) super.clone();
        // Preserve the current state
        cloned.bounds = new Rectangle(bounds);
        cloned.ammoLeft = this.ammoLeft;
        cloned.isReloading = this.isReloading;
        cloned.reloadStartTime = this.reloadStartTime;
        try {
            cloned.initializeSounds();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        return cloned;    }

    public void reload() {
        if (!isReloading && ammoLeft < MAGAZINE_SIZE) {
            isReloading = true;
            reloadStartTime = System.currentTimeMillis();
            playReloadSound();
        }
    }

    public void dispose() {
        if (shootSound != null) {
            shootSound.close();
        }
        if (reloadSound != null) {
            reloadSound.close();
        }
    }

    public int getRarity() {
        int chance = random.nextInt(100); // Generate a random number between 0 and 99

        if (chance < 40) {
            rarity = "common";
            return 0; // 40% chance
        } else if (chance < 65) {
            rarity = "uncommon";
            return 1; // 30% chance
        } else if (chance < 85) {
            rarity = "rare";
            return 2; // 20% chance
        } else if (chance < 95) {
            rarity = "epic";
            return 3; // 10% chance
        } else {
            rarity = "legendary";
            return 4; // 5% chance
        }
    }

    public void adjustStatsBasedOnRarity() {
        switch (rarity) {
            case "common":
                // No changes for common
                break;
            case "uncommon":
                RELOAD_TIME *= 0.9; // Decrease reload time by 10%
                FIRE_RATE *= 1.1; // Increase fire rate by 10%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.1); // Increase magazine size by 10%
                break;
            case "rare":
                RELOAD_TIME *= 0.85; // Decrease reload time by 15%
                FIRE_RATE *= 1.2; // Increase fire rate by 20%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.2); // Increase magazine size by 20%
                break;
            case "epic":
                RELOAD_TIME *= 0.8; // Decrease reload time by 20%
                FIRE_RATE *= 1.3; // Increase fire rate by 30%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.3); // Increase magazine size by 30%
                break;
            case "legendary":
                RELOAD_TIME *= 0.7; // Decrease reload time by 30%
                FIRE_RATE *= 1.4; // Increase fire rate by 40%
                MAGAZINE_SIZE = (int) (MAGAZINE_SIZE * 1.4); // Increase magazine size by 40%
                break;
            default:
                throw new IllegalArgumentException("Unknown rarity: " + rarity);
        }
    }

    protected BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path), "Image not found: " + path));
    }

    public void updateReload() {
        if (isReloading) {
            long now = System.currentTimeMillis();
            if (now - reloadStartTime > RELOAD_TIME * 1000) {
                finishReload();
            }
        }
    }

    public void finishReload() {
        // Only refill ammo if the weapon is not dropped
        if (ammoLeft < MAGAZINE_SIZE) {
            ammoLeft = MAGAZINE_SIZE; // Refill ammo to magazine size
        }
        isReloading = false;
    }

    public void draw(Graphics2D g2, GamePanel gamePanel) {
        int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;
        if (worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY) {
            g2.drawImage(iconImage, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
        }
        if (Math.sqrt(Math.pow(gamePanel.player.playerX - worldX, 2) +
                Math.pow(gamePanel.player.playerY - worldY, 2)) <= gamePanel.tileSize) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Press E to pick up " + weaponName,
                    screenX, screenY - 10);
        }
    }

    public void updateBounds() {
        bounds.x = worldX;
        bounds.y = worldY;
    }

    protected BufferedImage resizeImage(BufferedImage image, int width, int height) {
        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());

        // Draw the original image to the resized image
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    protected BufferedImage resizeImage(BufferedImage image) {
        BufferedImage resizedImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }
}