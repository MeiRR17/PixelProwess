package object;

public class Shotgun extends Weapon{
    public Shotgun() {
        super("object/weapon/combat/shotgun/shotgun.png", "object/weapon/icon/shotgun/shotgun.png", "shotgun");
    }

    @Override
    public void setTileIcon() {
        System.out.println("Setting SHOTGUN icon on tile: " + iconImagePath);
    }
}
