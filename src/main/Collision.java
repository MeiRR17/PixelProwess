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

//    public int checkObject(Entity entity, boolean playerCollision) {
//        int index = 999;
//
//        for (int i = 0; i < gamePanel.objMaster.length; i++) {
//            if(gamePanel.objMaster[i] != null) {
//                entity.bounds.x = entity.playerX + entity.bounds.x;
//                entity.bounds.y = entity.playerY + entity.bounds.y;
//
//                gamePanel.objMaster[i].bounds.x = gamePanel.objMaster[i].bounds.x + gamePanel.objMaster[i].bounds.x;
//                gamePanel.objMaster[i].bounds.y = gamePanel.objMaster[i].bounds.y + gamePanel.objMaster[i].bounds.y;
//
//                switch (entity.direction) {
//                    case "left":
//                        entity.bounds.y -= entity.speed;
//                        if(entity.bounds.intersects(gamePanel.objMaster[i].bounds)) {
//                            System.out.println("left collision");
//                        }
//                        break;
//                    case "right":
//                        entity.bounds.y += entity.speed;
//                        if(entity.bounds.intersects(gamePanel.objMaster[i].bounds)) {
//                            System.out.println("right collision");
//                        }
//                        break;
//                    case "up":
//                        entity.bounds.x -= entity.speed;
//                        if(entity.bounds.intersects(gamePanel.objMaster[i].bounds)) {
//                            System.out.println("up collision");
//                        }
//                        break;
//                    case "down":
//                        entity.bounds.x += entity.speed;
//                        if(entity.bounds.intersects(gamePanel.objMaster[i].bounds)) {
//                            System.out.println("down collision");
//                        }
//                        break;
//                }
//            }
//            entity.bounds.x += entity.solidAreaDefaultX;
//            entity.bounds.y += entity.solidAreaDefaultY;
//            gamePanel.objMaster[i].bounds.x = gamePanel.objMaster[i].solidAreaDefaultX;
//            gamePanel.objMaster[i].bounds.x = gamePanel.objMaster[i].solidAreaDefaultX;
//        }
//        return index;
//    }
}
