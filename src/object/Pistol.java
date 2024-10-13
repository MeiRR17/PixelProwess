package object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Pistol extends ObjMaster{
    public Pistol() throws IOException {
        name = "pistol";
        BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("weapon/icon/pistol1.png")));
        image = resizeImage(img, 32, 32);
        collision = true;
    }
}
