package object.weapons;

import java.io.IOException;

public class Shotgun extends Weapon {
    public Shotgun() throws IOException {
        super("object/weapon/combat/shotgun/shotgun.png", 1, "object/weapon/icon/shotgun/shotgun.png", "shotgun", "object/weapon/bullet/shotgun.png", 46, 5, 1, 5);
    }
}
