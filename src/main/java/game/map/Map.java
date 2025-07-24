package game.map;

import main.menu.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Map {
    GamePanel gamePanel;
    public static Tile[] tiles;
    public static int[][] mapNumber;
    public Rectangle[][] collisionBounds;
    public Polygon[][] customCollisionBounds;
    private String currentMapPath;


    public Map(GamePanel gamePanel) throws IOException {
        this.gamePanel = gamePanel;
        tiles = new Tile[40];

        // Initialize all tiles with a default empty tile
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
            tiles[i].customCollision = false;
            tiles[i].collision = false;
        }

        mapNumber = new int[gamePanel.worldColumn][gamePanel.worldRow];
        collisionBounds = new Rectangle[gamePanel.worldColumn][gamePanel.worldRow];
        customCollisionBounds = new Polygon[gamePanel.worldColumn][gamePanel.worldRow];

        getTileImage();
        loadFromFile("/maps/lake.txt");
    }
    // Load the map from a text file and parse it into the mapNumber array
    public void loadFromFile(String path) throws IOException {
        this.currentMapPath = path;
        InputStream is = getClass().getResourceAsStream(path);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        int column = 0;
        int row = 0;

        while (column < gamePanel.worldColumn && row < gamePanel.worldRow) {
            String line = br.readLine();

            while (column < gamePanel.worldColumn) {
                String[] numbers = line.split(" ");
                int tileCode = Integer.parseInt(numbers[column]);
                mapNumber[column][row] = tileCode;

                //  set collision for specific tiles
                if (tileCode == 1 || tileCode == 2 ||
                        tileCode == 28 || tileCode == 29 ||
                        tileCode == 30 || tileCode == 5 ||
                        tileCode == 4 || tileCode == 31) {
                    collisionBounds[column][row] = new Rectangle(column * gamePanel.tileSize, row * gamePanel.tileSize, gamePanel.tileSize, gamePanel.tileSize);
                }

                column++;
            }

            if (column == gamePanel.worldColumn) {
                column = 0;
                row++;
            }
        }

        br.close();

        for (column = 0; column < gamePanel.worldColumn; column++) {
            for (row = 0; row < gamePanel.worldRow; row++) {
                int tileCode = mapNumber[column][row];
                if (tiles[tileCode].customCollision) {
                    createCustomCollisionBound(column, row, tileCode);
                }
            }
        }
    }

    // Draw the map on the screen by iterating through the mapNumber array
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gamePanel.worldColumn && worldRow < gamePanel.worldRow) {
            int tileNum = mapNumber[worldCol][worldRow];
            int worldX = worldCol * gamePanel.tileSize;
            int worldY = worldRow * gamePanel.tileSize;
            int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;

            // Only draw if the tile is visible on screen
            if (isVisible(worldX, worldY)) {
                g2.drawImage(tiles[tileNum].image, screenX, screenY,
                        gamePanel.tileSize, gamePanel.tileSize, null);

                // Draw regular collision boundaries for debugging
                if (tiles[tileNum].collision) {
                    if (gamePanel.debug) {
                        g2.setColor(Color.RED);
                        // Adjust the coordinates to ensure the rectangle surrounds the entire tile
                        g2.drawRect(screenX, screenY, gamePanel.tileSize - 1, gamePanel.tileSize - 1);
                    }


                    // Draw custom collision boundaries for debugging
                    if (tiles[tileNum].customCollision) {
                        g2.setColor(Color.RED);
                        Polygon screenPoly = getScreenPolygon(customCollisionBounds[worldCol][worldRow],
                                gamePanel.player.playerX,
                                gamePanel.player.playerY);
                        g2.drawPolygon(screenPoly);
                    }
                }
            }

            worldCol++;
            if (worldCol == gamePanel.worldColumn) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

    private void createCustomCollisionBound(int column, int row, int tileCode) {
        int[][] shape = tiles[tileCode].collisionShape;
        int[] xPoints = new int[shape.length];
        int[] yPoints = new int[shape.length];

        for (int i = 0; i < shape.length; i++) {
            xPoints[i] = column * gamePanel.tileSize + shape[i][0];
            yPoints[i] = row * gamePanel.tileSize + shape[i][1];
        }

        customCollisionBounds[column][row] = new Polygon(xPoints, yPoints, shape.length);
    }

    private boolean isVisible(int worldX, int worldY) {
        return worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY;
    }

    private Polygon getScreenPolygon(Polygon worldPoly, int playerX, int playerY) {
        int[] xPoints = worldPoly.xpoints.clone();
        int[] yPoints = worldPoly.ypoints.clone();

        for (int i = 0; i < xPoints.length; i++) {
            xPoints[i] = xPoints[i] - playerX + gamePanel.player.screenX;
            yPoints[i] = yPoints[i] - playerY + gamePanel.player.screenY;
        }

        return new Polygon(xPoints, yPoints, worldPoly.npoints);
    }
    // Load tile images from the resources
    public void getTileImage() throws IOException {
        // Tile 0 - Bottom left down mountain
        tiles[0] = new Tile();
        tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/cliff/bottom_left_downGrass.png")));
        tiles[0].customCollision = true;
        tiles[0].collisionShape = new int[][]{
                {0, gamePanel.tileSize / 6},    // Top-left
                {48, 0},   // Top-right
                {48, 48},  // Bottom-right
                {24, 48},  // Bottom-middle
                {0, 24}    // Middle-left
        };


        // Tile 1 - Bottom left up mountain
        tiles[1] = new Tile();
        tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/cliff/bottom_left_upGrass.png")));
        tiles[1].collision = true;

        // Tile 2 - Bottom right down mountain
        tiles[2] = new Tile();
        tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/cliff/bottom_right_downGrass.png")));
        tiles[2].collision = true;

        // Tile 3 - Bottom right up mountain
        tiles[3] = new Tile();
        tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/cliff/bottom_right_upGrass.png")));
        tiles[3].collision = true;

        // Tile 4 - Cactus 2 handed
        tiles[4] = new Tile();
        tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/dead_grass/cactus/c1.png")));
        tiles[4].collision = true;

        // Tile 5 - Cactus 4 handed
        tiles[5] = new Tile();
        tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/dead_grass/cactus/c2.png")));
        tiles[5].collision = true;

        // Tile 6 - normal chest
        tiles[6] = new Tile();
        tiles[6].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/place/chest/commonGrass.png")));
        tiles[6].collision = true;

        // Tile 7 - dead grass right
        tiles[7] = new Tile();
        tiles[7].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/dead_grass/dg1.png")));

        // Tile 8 - dead grass left
        tiles[8] = new Tile();
        tiles[8].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/dead_grass/dg2.png")));



        // Tile 10 - water
        tiles[10] = new Tile();
        tiles[10].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/full.png")));
        tiles[10].collision = true;

        // Tile 11 - dead grass
        tiles[11] = new Tile();
        tiles[11].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/dead_grass/full.png")));

        // Tile 12 - grass
        tiles[12] = new Tile();
        tiles[12].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/full.png")));

        // Tile 13 - grass right
        tiles[13] = new Tile();
        tiles[13].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/g1.png")));

        // Tile 14 - grass left
        tiles[14] = new Tile();
        tiles[14].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/g2.png")));






        // Tile 21 - legendary chest
        tiles[21] = new Tile();
        tiles[21].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/place/chest/legenGrass.png")));
        tiles[21].collision = true;
        // Tile 22 - rare chest
        tiles[22] = new Tile();
        tiles[22].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/place/chest/rareGrass.png")));
        tiles[22].collision = true;




        // Tile 32 - outward (up) water cliff
        tiles[32] = new Tile();
        tiles[32].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/up.png")));
        tiles[32].customCollision = true;
        tiles[32].collisionShape = new int[][]{
                {0, 0},
                {gamePanel.tileSize, 0},
                {gamePanel.tileSize, gamePanel.tileSize / 2},
                {0, gamePanel.tileSize / 2}
        };

        // Tile 9 - outward (down) water cliff
        tiles[9] = new Tile();
        tiles[9].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/down.png")));
        tiles[9].customCollision = true;
        tiles[9].collisionShape = new int[][]{
                {0, 16},
                {64, 16},
                {64, 64},
                {0, 64}
        };

        // Tile 16 - outward (left) water cliff
        tiles[16] = new Tile();
        tiles[16].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/left.png")));
        tiles[16].customCollision = true;
        tiles[16].collisionShape = new int[][]{
                {0, 0},
                {36, 0},
                {36, 64},
                {0, 64}
        };

        // Tile 23 - outward (right) water cliff
        tiles[23] = new Tile();
        tiles[23].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/right.png")));
        tiles[23].customCollision = true;
        tiles[23].collisionShape = new int[][]{
                {36, 0},
                {64, 0},
                {64, 64},
                {36, 64}
        };

        // Tile 15 - outward (left-down) water cliff
        tiles[15] = new Tile();
        tiles[15].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/left_down.png")));
        tiles[15].customCollision = true;
        tiles[15].collisionShape = new int[][]{
                {0, 0},
                {48, 0},
                {48, 16},
                {64, 16},
                {64, 64},
                {0, 64}
        };

        // Tile 24 - outward (right-down) water cliff
        tiles[24] = new Tile();
        tiles[24].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/right_down.png")));
        tiles[24].customCollision = true;
        tiles[24].collisionShape = new int[][]{
                {36, 0},
                {64, 0},
                {64, 64},
                {0, 64},
                {0, 16},
                {36, 16}
        };

        //Tile 19 - outward (left-up) water cliff
        tiles[19] = new Tile();
        tiles[19].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/left_up.png")));
        tiles[19].customCollision = true;
        tiles[19].collisionShape = new int[][]{
                {0, 0},
                {64, 0},
                {64, 34},
                {48, 34},
                {24, 64},
                {0, 64}
        };

        // Tile 26 - outward (right-up) water cliff
        tiles[26] = new Tile();
        tiles[26].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/outward/right_up.png")));
        tiles[26].customCollision = true;
        tiles[26].collisionShape = new int[][]{
                {0, 0},
                {64, 0},
                {64, 64},
                {40, 64},
                {16, 32},
                {0, 32}
        };


        // Tile 20 - inward (left-up) mountain
        tiles[20] = new Tile();
        tiles[20].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/inward/left_up.png")));
        tiles[20].customCollision = true;
        tiles[20].collisionShape = new int[][]{
                {36, 40},
                {64, 30},
                {64, 64},
                {36, 64}
        };

        // Tile 17 - inward (left-down) water cliff
        tiles[17] = new Tile();
        tiles[17].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/inward/left_down.png")));
        tiles[17].customCollision = true;
        tiles[17].collisionShape = new int[][]{
                {40, 0},
                {64, 0},
                {64, 32}
        };

        // Tile 27 - inward (right-up) water cliff
        tiles[27] = new Tile();
        tiles[27].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/inward/right_up.png")));
        tiles[27].customCollision = true;
        tiles[27].collisionShape = new int[][]{
                {0, 26},
                {32, 64},
                {0, 64}
        };

        // Tile 25 - inward (right-down) water cliff
        tiles[25] = new Tile();
        tiles[25].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/water/cliff/inward/right_down.png")));
        tiles[25].customCollision = true;
        tiles[25].collisionShape = new int[][]{
                {0, 0},
                {24, 0},
                {12, 32},
                {0, 32},
        };




        // Tile 28 - rock 1
        tiles[28] = new Tile();
        tiles[28].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/rock/rock1.png")));
        tiles[28].collision = true;

        // Tile 29 - rock 2
        tiles[29] = new Tile();
        tiles[29].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/rock/rock2.png")));
        tiles[29].collision = true;

        // Tile 30 - tree 1
        tiles[30] = new Tile();
        tiles[30].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/tree/t1.png")));
        tiles[30].customCollision = true;
        tiles[30].collisionShape = new int[][]{
                {8, 4},
                {56, 4},
                {56, 56},
                {8, 56}
        };


        // Tile 31 - tree 2
        tiles[31] = new Tile();
        tiles[31].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/newTiles/ground/grass/tree/t2.png")));
        tiles[31].collision = true;

    }
    public String getCurrentMapPath() {
        return currentMapPath;
    }

}
