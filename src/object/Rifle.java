package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Rifle extends Weapon {
    public Rifle() {
        super("object/weapon/combat/rifle/scar.png", "object/weapon/icon/rifle/rifle.png", "shotgun");
    }

    @Override
    public void setTileIcon() {
        System.out.println("Setting SHOTGUN icon on tile: " + iconImagePath);
    }
}
