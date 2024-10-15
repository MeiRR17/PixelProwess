package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Scar extends Gun {
    public Scar() throws IOException {
        name = "scar";
        image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("object/weapon/combat/rifle/scar.png")));
    }
}
