package game.object.weapon.weapons;

import game.object.weapon.Big;

import java.io.IOException;

public class Scar extends Big {
    public Scar() throws IOException {
        super("object/weapon/combat/rifle/Scar.png", 1,
                new String[]{"object/weapon/icon/rifle/Scar/common.png",
                        "object/weapon/icon/rifle/Scar/uncommon.png",
                        "object/weapon/icon/rifle/Scar/rare.png",
                        "object/weapon/icon/rifle/Scar/epic.png",
                        "object/weapon/icon/rifle/Scar/legendary.png"},
                "scar",
                "object/weapon/bullet/rifle.png",
                33,
                30,
                6,
                2.5,
                false);
    }
}
