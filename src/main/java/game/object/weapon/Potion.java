package game.object.weapon;

import game.entity.Player;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class Potion {
    public String type;
    public BufferedImage image;

    public Potion(String type) throws IOException {
        this.type = type;
        loadImage();
    }

    private void loadImage() throws IOException {
        String imagePath = "UI/potions/" + type + " potion.png";
        image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(imagePath)));
    }

    public void use(Player player) {
        switch(type) {
            case "health" -> player.health = Math.min(player.health + 50, player.maxHealth);
            case "shield" -> player.addShield(50);
            case "ulti" -> {
                player.health = Math.min(player.health + 50, player.maxHealth);
                player.addShield(50);
            }
        }
    }
}