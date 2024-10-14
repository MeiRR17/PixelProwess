package object;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Scar extends ObjMaster {
    private BufferedImage image;

    public Scar() throws IOException {
        name = "scar";
        image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/game/scar.png")));
    }

    public void draw(Graphics2D g, GamePanel gamePanel, double angle) {

        int radius = 85;

        int playerCenterX = gamePanel.player.screenX + gamePanel.player.playerWidth / 2;
        int playerCenterY = gamePanel.player.screenY + gamePanel.player.playerHeight / 2;

        int weaponX = playerCenterX + (int) (radius * Math.cos(angle)) - image.getWidth() / 2;
        int weaponY = playerCenterY + (int) (radius * Math.sin(angle)) - image.getHeight() / 2;

        // Translate the graphics context to the calculated weapon position
        g.translate(weaponX + image.getWidth() / 2, weaponY + image.getHeight() / 2);

        // Rotate the weapon image based on the angle
        g.rotate(angle);

        g.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);

        // Reset the graphics context after drawing
        g.rotate(-angle);
        g.translate(-(weaponX + image.getWidth() / 2), -(weaponY + image.getHeight() / 2));
    }
}
