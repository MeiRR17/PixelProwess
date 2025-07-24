package game.object.weapon.weapons;

import game.object.weapon.Big;

import java.io.IOException;

public class AK extends Big {
    public AK() throws IOException {
        super("object/weapon/combat/rifle/AK-47.png", 1,
                new String[]{"object/weapon/icon/rifle/AK/common.png",
                        "object/weapon/icon/rifle/AK/uncommon.png",
                        "object/weapon/icon/rifle/AK/rare.png",
                        "object/weapon/icon/rifle/AK/epic.png",
                        "object/weapon/icon/rifle/AK/legendary.png"},
                "ak",
                "object/weapon/bullet/rifle.png",
                35,
                25,
                4,
                3,
                false);
    }
}
