package object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Rifle extends Gun{
    public Rifle() throws IOException {
        name = "rifle";
        image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/game/AK-47.png")));
    }
}
