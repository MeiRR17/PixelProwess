package object;

public abstract class Weapon {
    protected String gunImagePath;  // Image used in-game
    protected String iconImagePath; // Image used as tile icon
    protected String weaponName;

    public Weapon(String gunImagePath, String iconImagePath, String weaponName) {
        this.gunImagePath = gunImagePath;
        this.iconImagePath = iconImagePath;
        this.weaponName = weaponName;
    }

    public String getGunImagePath() {
        return gunImagePath;
    }

    public String getIconImagePath() {
        return iconImagePath;
    }

    public String getWeaponName() {
        return weaponName;
    }

    // Each gun will handle its own tile icon logic
    public abstract void setTileIcon();
}
