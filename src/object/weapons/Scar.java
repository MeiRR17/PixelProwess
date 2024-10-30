package object.weapons;

import object.Gun;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Scar extends Weapon {
    public Scar() throws IOException {
        super("object/weapon/combat/rifle/Scar.png", "object/weapon/icon/rifle/Scar.png", "scar", true,  33, 30, 6, 2.5);
    }
}
