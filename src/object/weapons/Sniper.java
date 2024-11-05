package object.weapons;

import java.io.IOException;

public class Sniper extends Weapon{
    public Sniper() throws IOException {
        super("object/weapon/combat/sniper/Sniper.png", 3, "object/weapon/icon/sniper/Sniper.png", "sniper", "object/weapon/bullet/sniper.png", 121, 1, 1, 3);
    }
}
