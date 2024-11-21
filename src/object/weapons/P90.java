package object.weapons;

import java.io.IOException;

public class P90 extends Big{
    public P90() throws IOException {
        super("object/weapon/combat/SMG/P90.png",3,
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
                3);
    }
}
