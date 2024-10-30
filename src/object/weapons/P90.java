package object.weapons;

import java.io.IOException;

public class P90 extends Weapon{
    public P90() throws IOException {
        super("object/weapon/combat/SMG/P90.png", "object/weapon/icon/SMG/P90.png", "p90", true, 20, 40, 10, 3);
    }
}
