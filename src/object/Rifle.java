package object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Rifle extends ObjMaster{
    public Rifle() throws IOException {
        name = "rifle";
        BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/icon/AK-47.png")));
        image = resizeImage(img, 32, 32);
    }
}
