package object.weapons;

import java.io.IOException;

public class Scar extends Weapon {
    public Scar() throws IOException {
        super("object/weapon/combat/rifle/Scar.png", 3, "object/weapon/icon/rifle/Scar.png", "scar", "object/weapon/bullet/rifle.png",  33, 30, 6, 2.5);
    }
}
