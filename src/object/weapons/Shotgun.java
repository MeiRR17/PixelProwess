package object.weapons;

import java.io.IOException;

public class Shotgun extends Weapon {
    public Shotgun() throws IOException {
        super("object/weapon/combat/shotgun/shotgun.png", 3, "object/weapon/icon/shotgun/shotgun.png", "shotgun", "object/weapon/bullet/shotgun.png", 92, 5, 1, 5);
    }
}
