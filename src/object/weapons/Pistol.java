package object.weapons;

import java.io.IOException;

public class Pistol extends Weapon {
    public Pistol() throws IOException {
        super("object/weapon/combat/pistol/Makarov.png", 3,  "object/weapon/icon/pistol/pistol1.png", "pistol", "object/weapon/bullet/pistol.png", 24, 16, 5, 1.5);
    }
}
