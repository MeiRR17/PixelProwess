package game.object.weapon.weapons;

import game.object.weapon.Big;

import java.io.IOException;

public class Shotgun extends Big {
    public Shotgun() throws IOException {
        super("object/weapon/combat/shotgun/shotgun.png", 1,
                new String[]{"object/weapon/icon/shotgun/common.png",
                        "object/weapon/icon/shotgun/uncommon.png",
                        "object/weapon/icon/shotgun/rare.png",
                        "object/weapon/icon/shotgun/epic.png",
                        "object/weapon/icon/shotgun/legendary.png"},
                "shotgun",
                "object/weapon/bullet/shotgun.png",
                46,
                5,
                1,
                5,
                false);
    }
}
