package entity.ui;

import entity.Player;
import main.GamePanel;
import tile.TileManager;

public class Orc extends BaseMob {
    public Orc(GamePanel gamePanel, Player player, TileManager tileManager) {
        super(gamePanel, player, tileManager, 1.0, 1.1, 200);
    }

    @Override
    protected String getMobType() {
        return "orc";
    }

    @Override
    protected int getMaxHealth() {
        return 200;
    }
}