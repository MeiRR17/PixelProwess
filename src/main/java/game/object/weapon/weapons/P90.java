package game.object.weapon.weapons;

import game.object.weapon.Small;

import java.io.IOException;

public class P90 extends Small {
    public P90() throws IOException {
        super("object/weapon/combat/SMG/P90.png",2,
                new String[]{"object/weapon/icon/SMG/common.png",
                        "object/weapon/icon/SMG/uncommon.png",
                        "object/weapon/icon/SMG/rare.png",
                        "object/weapon/icon/SMG/epic.png",
                        "object/weapon/icon/SMG/legendary.png"},
                "p90",
                "object/weapon/bullet/smg.png",
                20,
                40,
                10,
                3,
                true);
    }
}
