package object.weapons;

import java.io.IOException;

public class Big extends Weapon {
    public Big(String gunImagePath, double scale, String[] iconImagePath, String weaponName, String bulletPath, int DAMAGE, int MAGAZINE_SIZE, double FIRE_RATE, double RELOAD_TIME) throws IOException {
        super(gunImagePath, scale, iconImagePath, weaponName, bulletPath, DAMAGE, MAGAZINE_SIZE, FIRE_RATE, RELOAD_TIME);
    }
}