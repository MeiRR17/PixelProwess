package game.object.mob;

import game.entity.Player;
import main.menu.GamePanel;
import game.map.Map;

public class Goblin extends Mob {
    public Goblin(GamePanel gamePanel, Player player, Map map, int playerLevel) {
        super(gamePanel, player, map, 1.3, 0.8, 100, 32, playerLevel);  // 32x32 size
    }

    @Override
    public String getMobType() {
        return "goblin";
    }

    @Override
    protected int getMaxHealth() {
        return 100;
    }
}