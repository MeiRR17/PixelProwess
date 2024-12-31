package entity.ui;

import entity.Player;
import main.GamePanel;
import tile.TileManager;

public class Goblin extends BaseMob {
    public Goblin(GamePanel gamePanel, Player player, TileManager tileManager) {
        super(gamePanel, player, tileManager, 1.3, 0.8, 100, 32);  // 32x32 size
    }

    @Override
    protected String getMobType() {
        return "goblin";
    }

    @Override
    protected int getMaxHealth() {
        return 100;
    }
}