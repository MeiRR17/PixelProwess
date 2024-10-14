package object;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Scar extends Gun {
    public Scar() throws IOException {
        name = "scar";
        image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/game/scar.png")));
    }
}
