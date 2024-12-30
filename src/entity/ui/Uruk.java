package entity.ui;

import entity.Player;
import main.GamePanel;
import tile.TileManager;

public class Uruk extends BaseMob {
    public Uruk(GamePanel gamePanel, Player player, TileManager tileManager) {
        super(gamePanel, player, tileManager, 1.0, 1.2, 300);
    }

    @Override
    protected String getMobType() {
        return "uruk";
    }

    @Override
    protected int getMaxHealth() {
        return 300;
    }
}