package game.entity.ui;

import game.entity.Player;
import game.object.weapon.Potion;
import game.object.weapon.Weapon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class InventoryDisplay {
    private final BufferedImage slotImage;
    private final BufferedImage equipLabel;
    private final BufferedImage panelImage; // Panel image
    private final int screenWidth;
    private final int screenHeight;
    private final int slotSize;
    private final int spacing;
    private final int panelWidth;
    private final int panelHeight;
    private final Player player;
    private BufferedImage currentDisplay;

    public InventoryDisplay(int screenWidth, int screenHeight, Player player) throws IOException {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.slotSize = screenHeight / 14;
        this.spacing = screenHeight / 20;
        this.player = player;

        // Load images
        slotImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("UI/inventory/inventoryBox.png")));
        equipLabel = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("UI/inventory/equipLabel.png")));
        panelImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("UI/inventory/panel1.png")));

        // Scale panel dimensions based on slot size
        panelWidth = (int) ((160.0 / 34.0) * slotSize);
        panelHeight = (int) ((52.0 / 34.0) * slotSize);

        // Initialize the display buffer
        currentDisplay = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        updateDisplay();
    }

    public void updateDisplay() {
        // Create a new graphics context for the buffer
        Graphics2D g2 = currentDisplay.createGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setComposite(AlphaComposite.SrcOver);

        // Redraw all elements
        drawInventory(g2);

        g2.dispose();
    }

    private void drawInventory(Graphics2D g2) {
        // Calculate the panel position (aligned fully at the bottom)
        int panelX = screenWidth / 2 - panelWidth / 2;
        int panelY = screenHeight - panelHeight;

        // Draw the panel
        g2.drawImage(panelImage, panelX, panelY, panelWidth, panelHeight, null);

        // Calculate positions for the slots on the panel (flush with the bottom)
        int y = screenHeight - slotSize - 5; // Move slots 5 pixels up
        int labelWidth = (int) ((13.0 / 34.0) * slotSize / 2); // Half the size of the original label
        int labelHeight = (int) ((14.0 / 34.0) * slotSize / 2); // Half the size of the original label

        // Left slot (20 pixels from the left edge of the panel)
        int leftX = panelX + 20;
        drawSlot(g2, leftX, y, labelWidth, labelHeight, player.bigWeapon == player.currentWeapon, "1", player.bigWeapon);

        // Center slot (aligned to the middle of the panel)
        int centerX = panelX + panelWidth / 2 - slotSize / 2;
        drawSlot(g2, centerX, y, labelWidth, labelHeight, player.smallWeapon == player.currentWeapon, "2", player.smallWeapon);

        // Right slot (20 pixels from the right edge of the panel)
        int rightX = panelX + panelWidth - slotSize - 20;
        drawPotionSlot(g2, rightX, y, labelWidth, labelHeight);
    }



    public void draw(Graphics2D g2) {
        // Draw the current display buffer
        g2.drawImage(currentDisplay, 0, 0, null);
    }

    // In InventoryDisplay class, modify the drawSlot method:
    private void drawSlot(Graphics2D g2, int x, int y, int labelWidth, int labelHeight, boolean isEquipped, String slotNumber, Weapon weapon) {
        // Draw base slot
        g2.drawImage(slotImage, x, y, slotSize, slotSize, null);

        // Draw weapon if present
        if (weapon != null) {
            g2.drawImage(weapon.iconImage, x + 5, y + 5, slotSize - 10, slotSize - 10, null);

            // Draw equip indicator if this weapon is the current weapon
            if (weapon == player.currentWeapon) {  // Changed this condition
                g2.drawImage(equipLabel,
                        x + (slotSize - labelWidth) / 2,
                        y - labelHeight, // Position directly above the slot
                        labelWidth,
                        labelHeight,
                        null);
            }

            // Draw ammo count
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
        }

        drawSlotNumber(g2, slotNumber, x, y);
    }

    private void drawPotionSlot(Graphics2D g2, int x, int y, int labelWidth, int labelHeight) {
        // Draw base slot
        g2.drawImage(slotImage, x, y, slotSize, slotSize, null);

        // Draw potion if available
        if (!player.potions.isEmpty()) {
            Potion currentPotion = player.potions.get(player.currentPotionIndex);
            g2.drawImage(currentPotion.image, x + 5, y + 5, slotSize - 10, slotSize - 10, null);

            // Draw potion count
            String count = String.valueOf(player.potions.size());
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(count, x + slotSize - fm.stringWidth(count) - 5, y + slotSize - 5);
        }

        // Draw equip label directly above the slot if needed
        if (player.currentPotionIndex == player.potions.size() - 1) {
            g2.drawImage(equipLabel,
                    x + (slotSize - labelWidth) / 2,
                    y - labelHeight, // Position directly above the slot
                    labelWidth,
                    labelHeight,
                    null);
        }

        drawSlotNumber(g2, "3", x, y);
    }

    private void drawSlotNumber(Graphics2D g2, String number, int x, int y) {
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(number, x + 5, y + fm.getHeight());
    }
}
