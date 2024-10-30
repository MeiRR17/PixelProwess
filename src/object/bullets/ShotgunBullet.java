package object.bullets;

import java.io.IOException;

public class ShotgunBullet extends Bullet {
    public ShotgunBullet(int x, int y, double angle) throws IOException {
        super("object/weapon/bullet/shotgun.png" , x, y, angle);
    }
}
