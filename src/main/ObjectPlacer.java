package main;

import object.weapons.*;

import java.io.IOException;

public class ObjectPlacer {

    GamePanel gamePanel;

    public ObjectPlacer(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void placeObjects() throws IOException {

        gamePanel.weapons[0] = new TacticalAssaultRifle();
        gamePanel.weapons[0].worldX = 40 * gamePanel.tileSize;
        gamePanel.weapons[0].worldY = 40 * gamePanel.tileSize;

        gamePanel.weapons[1] = new Shotgun();
        gamePanel.weapons[1].worldX = 41 * gamePanel.tileSize;
        gamePanel.weapons[1].worldY = 40 * gamePanel.tileSize;

        gamePanel.weapons[2] = new Pistol();
        gamePanel.weapons[2].worldX = 42 * gamePanel.tileSize;
        gamePanel.weapons[2].worldY = 40 * gamePanel.tileSize;

        gamePanel.weapons[3] = new AK();
        gamePanel.weapons[3].worldX = 43 * gamePanel.tileSize;
        gamePanel.weapons[3].worldY = 40 * gamePanel.tileSize;

        gamePanel.weapons[4] = new Automatic_Sniper();
        gamePanel.weapons[4].worldX = 44 * gamePanel.tileSize;
        gamePanel.weapons[4].worldY = 40 * gamePanel.tileSize;

        gamePanel.weapons[5] = new Sniper();
        gamePanel.weapons[5].worldX = 45 * gamePanel.tileSize;
        gamePanel.weapons[5].worldY = 40 * gamePanel.tileSize;

        gamePanel.weapons[7] = new P90();
        gamePanel.weapons[7].worldX = 41 * gamePanel.tileSize;
        gamePanel.weapons[7].worldY = 41 * gamePanel.tileSize;

        gamePanel.weapons[8] = new Scar();
        gamePanel.weapons[8].worldX = 42 * gamePanel.tileSize;
        gamePanel.weapons[8].worldY = 41 * gamePanel.tileSize;


    }
}
