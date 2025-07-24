package game.object.mob;

import game.entity.Player;
import main.menu.GamePanel;
import game.map.Map;

public class Uruk extends Mob {
    public Uruk(GamePanel gamePanel, Player player, Map map, int playerLevel) {
        super(gamePanel, player, map, 1.1, 1.2, 350, 64, playerLevel);  // 64x64 size
    }

    @Override
    public String getMobType() {
        return "uruk";
    }

    @Override
    protected int getMaxHealth() {
        return 450;
    }
}