package object.weapons;

import java.io.IOException;

public class Sniper extends Weapon{
    public Sniper() throws IOException {
        super("object/weapon/combat/sniper/Sniper.png", "object/weapon/icon/sniper/Sniper.png", "sniper", true, 121, 1, 1, 3);
    }
}
