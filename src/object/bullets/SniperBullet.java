package object.bullets;

import java.io.IOException;

public class SniperBullet extends Bullet {
    public SniperBullet(int x, int y, double angle) throws IOException {
        super("object/weapon/bullet/sniper.png" , x, y, angle);
    }
}
