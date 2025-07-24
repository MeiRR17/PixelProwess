package game.object.weapon.weapons;

import game.object.weapon.Small;

import java.io.IOException;

public class Pistol extends Small {
    public Pistol() throws IOException {
        super("object/weapon/combat/pistol/pistol.png", 1,
                new String[]{"object/weapon/icon/pistol/common.png",
                        "object/weapon/icon/pistol/uncommon.png",
                        "object/weapon/icon/pistol/rare.png",
                        "object/weapon/icon/pistol/epic.png",
                        "object/weapon/icon/pistol/legendary.png"},
                "pistol",
                "object/weapon/bullet/pistol.png",
                24,
                16,
                5,
                1.5,
                false);
    }
}
