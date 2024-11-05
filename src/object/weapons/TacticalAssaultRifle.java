package object.weapons;

import java.io.IOException;

public class TacticalAssaultRifle extends Weapon {
    public TacticalAssaultRifle() throws IOException {
        super("object/weapon/combat/rifle/Pindad_SS1.png", 3, "object/weapon/icon/rifle/Pindad_SS1.png", "tacticalAssaultRifle", "object/weapon/bullet/rifle.png", 30, 30, 7, 2.7);
    }
}
