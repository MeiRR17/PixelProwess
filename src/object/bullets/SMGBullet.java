package object.bullets;

import java.io.IOException;

public class SMGBullet extends Bullet {
    public SMGBullet(int x, int y, double angle) throws IOException {
        super("object/weapon/bullet/smg.png" , x, y, angle);
    }
}
