package entity.ui;

import entity.Player;
import main.GamePanel;
import tile.TileManager;

public class Satan extends BaseMob {
    public Satan(GamePanel gamePanel, Player player, TileManager tileManager) {
        super(gamePanel, player, tileManager, 0.7, 1.8, 500, 48);  // 48x48 size
    }

    @Override
    protected String getMobType() {
        return "satan";
    }

    @Override
    protected int getMaxHealth() {
        return 500;
    }
}
