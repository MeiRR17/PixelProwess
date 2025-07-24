package game.map;

import game.entity.Entity;
import main.menu.GamePanel;

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
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityTopRow, entityTopRow, "up");
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityLeftColumn, entityTopRow, entityBottomRow, "left");
                break;
            case "down&right":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityBottomRow, entityBottomRow, "down");
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityRightColumn, entityRightColumn, entityTopRow, entityBottomRow, "right");
                break;
            case "down&left":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityRightColumn, entityBottomRow, entityBottomRow, "down");
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                checkCollisionTiles(entity, entityLeftColumn, entityLeftColumn, entityTopRow, entityBottomRow, "left");
                break;
        }
    }

    private void checkCollisionTiles(Entity entity, int colStart, int colEnd, int rowStart, int rowEnd, String direction) {
        for (int col = colStart; col <= colEnd; col++) {
            for (int row = rowStart; row <= rowEnd; row++) {
                if (col >= 0 && col < gamePanel.worldColumn && row >= 0 && row < gamePanel.worldRow) {
                    int tileNum = gamePanel.map.mapNumber[col][row];

                    // Check for regular collision
                    if (gamePanel.map.tiles[tileNum].collision) {
                        entity.playerCollision = true;
                        return;
                    }

                    // Check for custom collision
                    if (gamePanel.map.tiles[tileNum].customCollision) {
                        Polygon customBounds = gamePanel.map.customCollisionBounds[col][row];
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
            if (gamePanel.map.collisionBounds[worldCol][worldRow] != null) {
                // Check if the entity intersects with this tile's collision bounds
                if (rect1.intersects(gamePanel.map.collisionBounds[worldCol][worldRow])) {
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
}
