package object.weapons;

import java.io.IOException;

public class AK extends Weapon{
    public AK() throws IOException {
        super("object/weapon/combat/rifle/AK-47.png", 3, "object/weapon/icon/rifle/AK-47.png", "ak", "object/weapon/bullet/rifle.png", 35, 25, 4, 3);
    }
}