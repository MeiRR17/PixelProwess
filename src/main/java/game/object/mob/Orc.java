package game.object.mob;

import game.entity.Player;
import main.menu.GamePanel;
import game.map.Map;

public class Orc extends Mob {
    public Orc(GamePanel gamePanel, Player player, Map map, int playerLevel) {
        super(gamePanel, player, map, 1.0, 1.2, 250, 48, playerLevel);  // 48x48 size
    }

    @Override
    public String getMobType() {
        return "orc";
    }

    @Override
    protected int getMaxHealth() {
        return 250;
    }
}

