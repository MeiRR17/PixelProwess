package entity;

import object.Rifle;
import object.Scar;
import object.Weapon;

import java.io.IOException;
import java.util.Random;

public class PickGun {
    private final Player player;
    private final Random random;

    public PickGun(Player player) {
        this.player = player;
        this.random = new Random();
    }

    public void getGun() throws IOException {
        // Generate a random number (0 or 1)
        int randomGun = random.nextInt(3);

        Weapon selectedGun;

    }
}
