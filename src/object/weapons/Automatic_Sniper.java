package object.weapons;

import java.io.IOException;

public class Automatic_Sniper extends Weapon{
    public Automatic_Sniper() throws IOException {
        super("object/weapon/combat/sniper (optional - in game)/Automatic_Sniper.png", 3, "object/weapon/icon/sniper (optional - in game)/Automatic_Sniper.png", "automaticSniper", "object/weapon/bullet/rifle.png", 38, 16, 4, 4);
    }
}
