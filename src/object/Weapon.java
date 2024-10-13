package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Weapon extends ObjMaster {
    GamePanel gamePanel;
    public Weapon() throws IOException {
        name = "pistol";
        BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/game/pistol1.png")));
        image = resizeImage(img, 32, 32);
    }
}
