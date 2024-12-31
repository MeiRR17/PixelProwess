package entity.ui;

import entity.Player;
import main.GamePanel;
import tile.TileManager;

public class Uruk extends BaseMob {
    public Uruk(GamePanel gamePanel, Player player, TileManager tileManager) {
        super(gamePanel, player, tileManager, 1.1, 1.2, 350, 64);  // 64x64 size
    }

    @Override
    protected String getMobType() {
        return "uruk";
    }

    @Override
    protected int getMaxHealth() {
        return 450;
    }
}