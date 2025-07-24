package game.object.mob;

import game.entity.Player;
import main.menu.GamePanel;
import game.map.Map;

public class Satan extends Mob {
    public Satan(GamePanel gamePanel, Player player, Map map, int playerLevel) {
        super(gamePanel, player, map, 0.7, 1.8, 500, 48, playerLevel);  // 48x48 size
    }

    @Override
    public String getMobType() {
        return "satan";
    }

    @Override
    protected int getMaxHealth() {
        return 500;
    }
}
