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
                // Moving up, subtract speed from the top world Y coordinate
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;

            case "down":
                // Moving down, add speed to the bottom world Y coordinate
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "left":
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "right":
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "up&right":
                // Check for UP collision
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }

                // Check for RIGHT collision
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "up&left":
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }

                // Check for LEFT collision
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "down&right":
                // Check for DOWN collision
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }

                // Check for RIGHT collision
                entityRightColumn = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityRightColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
            case "down&left":
                // Check for DOWN collision
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityRightColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }

                // Check for LEFT collision
                entityLeftColumn = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapNumber[entityLeftColumn][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.playerCollision = true;
                }
                break;
        }
    }

    public static boolean checkCollision (Rectangle rect1) {
        int entityLeftCol = rect1.x / gamePanel.tileSize;
        int entityRightCol = (rect1.x + rect1.width) / gamePanel.tileSize;
        int entityTopRow = rect1.y / gamePanel.tileSize;
        int entityBottomRow = (rect1.y + rect1.height) / gamePanel.tileSize;

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
