package object.bullets;

import java.io.IOException;

public class RifleBullet extends Bullet {
    public RifleBullet(int x, int y, double angle) throws IOException {
        super("object/weapon/bullet/rifle.png" , x, y, angle);
    }
}
