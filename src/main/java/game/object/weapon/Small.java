package game.object.weapon;

import java.io.IOException;

public class Small extends Weapon {
    public Small(String gunImagePath, double scale, String[] iconImagePath, String weaponName, String bulletPath, int DAMAGE, int MAGAZINE_SIZE, double FIRE_RATE, double RELOAD_TIME, boolean isAutomatic) throws IOException {
        super(gunImagePath, scale, iconImagePath, weaponName, bulletPath, DAMAGE, MAGAZINE_SIZE, FIRE_RATE, RELOAD_TIME, isAutomatic);
    }
}