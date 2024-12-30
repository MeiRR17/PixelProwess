package entity.ui;

import entity.Player;
import main.GamePanel;
import tile.TileManager;

public class Goblin extends BaseMob {
    // Update constructor to match usage
    public Goblin(GamePanel gamePanel, Player player, TileManager tileManager) {
        super(gamePanel, player, tileManager, 1.2, 0.8, 100);  // Pass hardcoded values to super
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