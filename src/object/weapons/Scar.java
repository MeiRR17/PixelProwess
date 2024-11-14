package object.weapons;

import java.io.IOException;

public class Scar extends Weapon {
    public Scar() throws IOException {
        super("object/weapon/combat/rifle/Scar.png", 3,
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
                2.5);
    }
}
