package main;

import entity.Entity;

import java.awt.*;

public class Collision {
    static GamePanel gamePanel;

    public Collision(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.playerX + entity.bounds.x;
        int entityRightWorldX = entity.playerX + entity.bounds.x + entity.bounds.width;
        int entityTopWorldY = entity.playerY + entity.bounds.y;
        int entityBottomWorldY = entity.playerY + entity.bounds.y + entity.bounds.height;

        int entityLeftColumn = entityLeftWorldX / gamePanel.tileSize;
        int entityRightColumn = entityRightWorldX / gamePanel.tileSize;
        int entityTopRow = entityTopWorldY / gamePanel.tileSize;
        int entityBottomRow = entityBottomWorldY / gamePanel.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityTopRow, "up");
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityBottomRow, entityBottomRow, "down");
                break;
            case "left":
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityLeftColumn, entityTopRow, entityBottomRow, "left");
                break;
            case "right":
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityRightColumn, entityRightColumn, entityTopRow, entityBottomRow, "right");
                break;
            case "up&right":
                // Check vertical movement first
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityTopRow, "up");
                // Then check horizontal movement
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityRightColumn, entityRightColumn, entityTopRow, entityBottomRow, "right");
                break;
            case "up&left":
                // Check vertical movement first
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityTopRow, "up");
                // Then check horizontal movement
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityLeftColumn, entityTopRow, entityBottomRow, "left");
                break;
            case "down&right":
                // Check vertical movement first
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityBottomRow, entityBottomRow, "down");
                // Then check horizontal movement
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityRightColumn, entityRightColumn, entityTopRow, entityBottomRow, "right");
                break;
            case "down&left":
                // Check vertical movement first
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityBottomRow, entityBottomRow, "down");
                // Then check horizontal movement
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityLeftColumn, entityTopRow, entityBottomRow, "left");
                break;
        }
    }

    private void checkCollisionTiles(Entity entity, int colStart, int colEnd, int rowStart, int rowEnd, String direction) {
        for (int col = colStart; col <= colEnd; col++) {
            for (int row = rowStart; row <= rowEnd; row++) {
                if (col >= 0 && col < gamePanel.worldColumn && row >= 0 && row < gamePanel.worldRow) {
                    int tileNum = gamePanel.tileManager.mapNumber[col][row];

                    // Check for regular collision
                    if (gamePanel.tileManager.tiles[tileNum].collision) {
                        entity.playerCollision = true;
                        return;
                    }

                    // Check for custom collision
                    if (gamePanel.tileManager.tiles[tileNum].customCollision) {
                        Polygon customBounds = gamePanel.tileManager.customCollisionBounds[col][row];
                        if (customBounds != null) {
                            // Create a Rectangle representing entity's next position
                            Rectangle nextPos = new Rectangle(
                                    entity.playerX + entity.bounds.x,
                                    entity.playerY + entity.bounds.y,
                                    entity.bounds.width,
                                    entity.bounds.height
                            );

                            // Adjust position based on direction and speed
                            switch (direction) {
                                case "up":
                                    nextPos.y -= entity.speed;
                                    break;
                                case "down":
                                    nextPos.y += entity.speed;
                                    break;
                                case "left":
                                    nextPos.x -= entity.speed;
                                    break;
                                case "right":
                                    nextPos.x += entity.speed;
                                    break;
                            }

                            // Check if the entity's next position intersects with the custom collision shape
                            if (customBounds.intersects(nextPos)) {
                                entity.playerCollision = true;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean checkCollision (Rectangle rect1) {
        boolean intersects = false;
        int worldCol = 0;
        int worldRow = 0;
        while (worldCol < gamePanel.worldColumn && worldRow < gamePanel.worldRow){
            if (gamePanel.tileManager.collisionBounds[worldCol][worldRow] != null) {
                // Check if the entity intersects with this tile's collision bounds
                if (rect1.intersects(gamePanel.tileManager.collisionBounds[worldCol][worldRow])) {
                    intersects = true;
                    break; // Stop once a collision is detected
                }
            }
            worldCol++;
            if (worldCol == gamePanel.worldColumn) {
                worldCol = 0;
                worldRow++;
            }
        }
        return intersects;
    }

    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gamePanel.weapons.length; i++) {
            if (gamePanel.weapons[i] != null) {
                entity.bounds.x = entity.playerX + entity.bounds.x;
                entity.bounds.y = entity.playerY + entity.bounds.y;
                // Get object's solid area position
                gamePanel.weapons[i].bounds.x = gamePanel.weapons[i].worldX + gamePanel.weapons[i].bounds.x;
                gamePanel.weapons[i].bounds.y = gamePanel.weapons[i].worldY + gamePanel.weapons[i].bounds.y;

                switch (entity.direction) {
                    case "up":
                        entity.bounds.y -= entity.speed;
                        if (entity.bounds.intersects(gamePanel.weapons[i].bounds)) {
                            if (gamePanel.weapons[i].collision) {
                                entity.playerCollision = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.bounds.y += entity.speed;
                        if (entity.bounds.intersects(gamePanel.weapons[i].bounds)) {
                            if (gamePanel.weapons[i].collision) {
                                entity.playerCollision = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.bounds.x -= entity.speed;
                        if (entity.bounds.intersects(gamePanel.weapons[i].bounds)) {
                            if (gamePanel.weapons[i].collision) {
                                entity.playerCollision = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.bounds.x += entity.speed;
                        if (entity.bounds.intersects(gamePanel.weapons[i].bounds)) {
                            if (gamePanel.weapons[i].collision) {
                                entity.playerCollision = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                }
                entity.bounds.x = entity.solidAreaDefaultX;
                entity.bounds.y = entity.solidAreaDefaultY;
                gamePanel.weapons[i].bounds.x = gamePanel.weapons[i].solidAreaDefaultX;
                gamePanel.weapons[i].bounds.y = gamePanel.weapons[i].solidAreaDefaultY;
            }
        }
        return index;
    }
}
