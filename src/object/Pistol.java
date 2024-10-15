package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Pistol extends ObjMaster {

    public Pistol() throws IOException {
        // Call the superclass constructor with the loaded image as the first statement
        super(ImageIO.read(Objects.requireNonNull(Pistol.class.getClassLoader().getResource("object/weapon/icon/pistol/pistol1.png"))));

        name = "pistol";
        collision = true;
    }
}
