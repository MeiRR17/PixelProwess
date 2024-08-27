package main;

import entity.Entity;

public class Collision {
    GamePanel gamePanel;

    public Collision(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.playerX + entity.bounds.x;
        int entityRightWorldX = entity.playerX + entity.bounds.x + entity.bounds.width;
        int entityTopWorldY = entity.playerY + entity.bounds.y;
        int entityBottomWorldY = entity.playerY + entity.bounds.y + entity.bounds.height;

        int entityLeftColumn = entityLeftWorldX/ gamePanel.tileSize;
        int entityRightColumn = entityRightWorldX/ gamePanel.tileSize;
        int entityTopRow = entityTopWorldY/ gamePanel.tileSize;
        int entityBottomRow = entityBottomWorldY/ gamePanel.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed)/ gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                if(gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY - entity.speed)/ gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if(gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "left":
                entityLeftColumn = (entityLeftWorldX - entity.speed)/ gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                if(gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "right":
                entityRightColumn = (entityRightWorldX + entity.speed)/ gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if(gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
        }
    }
}
