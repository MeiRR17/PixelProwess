package main;

import object.Rifle;
import object.Pistol;

import java.io.IOException;

public class ObjectPlacer {

    GamePanel gamePanel;

    public ObjectPlacer(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void placeObjects() throws IOException {
        gamePanel.objMaster[0] = new Pistol();
        gamePanel.objMaster[0].worldX = 26 * gamePanel.tileSize;
        gamePanel.objMaster[0].worldY = 26 * gamePanel.tileSize;

        gamePanel.objMaster[1] = new Rifle();
        gamePanel.objMaster[1].worldX = 27 * gamePanel.tileSize;
        gamePanel.objMaster[1].worldY = 26 * gamePanel.tileSize;
    }
}
