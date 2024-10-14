package entity;

import object.Rifle;
import object.Scar;

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
        int randomGun = random.nextInt(2);

        // Assign either a Scar or Rifle based on the random number
        if (randomGun == 0) {
            player.guns[0] = new Scar();
        } else {
            player.guns[0] = new Rifle();
        }
    }
}
