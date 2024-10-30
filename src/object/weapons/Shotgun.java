package object.weapons;

import java.io.IOException;

public class Shotgun extends Weapon {
    public Shotgun() throws IOException {
        super("object/weapon/combat/shotgun/shotgun.png", "object/weapon/icon/shotgun/shotgun.png", "shotgun", true, 92, 5, 1, 5);
    }
}
