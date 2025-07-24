package game.object.weapon.weapons;

import game.object.weapon.Big;

import java.io.IOException;

public class TacticalAssaultRifle extends Big {
    public TacticalAssaultRifle() throws IOException {
        super("object/weapon/combat/rifle/Pindad_SS1.png", 1.5,
                new String[]{"object/weapon/icon/rifle/Pindad/common.png",
                        "object/weapon/icon/rifle/Pindad/uncommon.png",
                        "object/weapon/icon/rifle/Pindad/rare.png",
                        "object/weapon/icon/rifle/Pindad/epic.png",
                        "object/weapon/icon/rifle/Pindad/legendary.png"},
                "tacticalAssaultRifle",
                "object/weapon/bullet/rifle.png",
                30,
                30,
                7,
                2.7,
                true);
    }
}
