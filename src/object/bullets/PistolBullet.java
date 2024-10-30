package object.bullets;

import java.io.IOException;

public class PistolBullet extends Bullet {
    public PistolBullet(int x, int y, double angle) throws IOException {
        super("object/weapon/bullet/pistol.png" , x, y, angle);
    }
}
