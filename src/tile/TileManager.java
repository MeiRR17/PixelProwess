package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManager {

    GamePanel gamePanel;
    public Tile[] tiles;
    public int[][] mapNumber;
    public Rectangle[][] collisionBounds;

    // Constructor to initialize the TileManager
    public TileManager(GamePanel gamePanel) throws IOException {
        this.gamePanel = gamePanel;
        tiles = new Tile[10]; // Assume max 10 types of tiles
        mapNumber = new int[gamePanel.worldColumn][gamePanel.worldRow]; // Map data array
        collisionBounds = new Rectangle[gamePanel.worldColumn][gamePanel.worldRow];

        getTileImage(); // Load the tile images
        load("res/maps/Map.DemoLines"); // Load the map layout
    }

    // Load tile images from the resources
    public void getTileImage() throws IOException {
        // Tile 0 - Bottom left down mountain
        tiles[0] = new Tile();
        tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/cliff (optional)/bottom_left_downGrass.png")));
        tiles[0].collision = true;

        // Tile 1 - Bottom left up mountain
        tiles[1] = new Tile();
        tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/cliff (optional)/bottom_left_upGrass.png")));
        tiles[1].collision = true;

        // Tile 2 - Bottom right down mountain
        tiles[2] = new Tile();
        tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/cliff (optional)/bottom_right_downGrass.png")));
        tiles[2].collision = true;

        // Tile 3 - Bottom right up mountain
        tiles[3] = new Tile();
        tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/cliff (optional)/bottom_right_upGrass.png")));
        tiles[3].collision = true;

        // Tile 4 - Cactus 2 handed
        tiles[4] = new Tile();
        tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/dead_grass/cactus/c1.png")));
        tiles[4].collision = true;

        // Tile 5 - Cactus 4 handed
        tiles[5] = new Tile();
        tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/dead_grass/cactus/c2.png")));
        tiles[5].collision = true;

        // Tile 7 - dead grass right
        tiles[7] = new Tile();
        tiles[7].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/dead_grass/dg1.png")));

        // Tile 8 - dead grass left
        tiles[8] = new Tile();
        tiles[8].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/dead_grass/dg2.png")));

        // Tile 9 - cliff outward down
        tiles[9] = new Tile();
        tiles[9].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/down.png")));
        tiles[9].collision = true;

        // Tile 10 - water
        tiles[10] = new Tile();
        tiles[10].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/full.png")));
        tiles[10].collision = true;

        // Tile 11 - dead grass
        tiles[11] = new Tile();
        tiles[11].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/dead_grass/full.png")));

        // Tile 12 - grass
        tiles[12] = new Tile();
        tiles[12].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/full.png")));

        // Tile 13 - grass right
        tiles[13] = new Tile();
        tiles[13].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/g1.png")));

        // Tile 14 - grass left
        tiles[14] = new Tile();
        tiles[14].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/g2.png")));

        // Tile 15 - outward left down mountain
        tiles[15] = new Tile();
        tiles[15].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/lefft_down.png")));
        tiles[15].collision = true;

        // Tile 16 - outward left mountain
        tiles[16] = new Tile();
        tiles[16].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/left.png")));
        tiles[16].collision = true;

        // Tile 17 - inward left down mountain
        tiles[17] = new Tile();
        tiles[17].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/inward/left_down.png")));
        tiles[17].collision = true;

        // Tile 19 - outward left up mountain
        tiles[19] = new Tile();
        tiles[19].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/left_up.png")));
        tiles[19].collision = true;

        // Tile 20 - inward left up mountain
        tiles[20] = new Tile();
        tiles[20].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/inward/left_up.png")));
        tiles[20].collision = true;

        // Tile 23 - outward right mountain
        tiles[23] = new Tile();
        tiles[23].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/right.png")));
        tiles[23].collision = true;

        // Tile 24 - outward right down mountain
        tiles[24] = new Tile();
        tiles[24].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/right_down.png")));
        tiles[24].collision = true;

        // Tile 25 - inward right down mountain
        tiles[25] = new Tile();
        tiles[25].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/inward/right_down.png")));
        tiles[25].collision = true;

        // Tile 26 - outward right up mountain
        tiles[26] = new Tile();
        tiles[26].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/right_up.png")));
        tiles[26].collision = true;

        // Tile 27 - inward right up mountain
        tiles[27] = new Tile();
        tiles[27].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/inward/right_up.png")));
        tiles[27].collision = true;

        // Tile 28 - rock 1
        tiles[28] = new Tile();
        tiles[28].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/rock/rock1.png")));
        tiles[28].collision = true;

        // Tile 29 - rock 2
        tiles[29] = new Tile();
        tiles[29].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/rock/rock2.png")));
        tiles[29].collision = true;

        // Tile 30 - tree 1
        tiles[30] = new Tile();
        tiles[30].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/tree/t1.png")));
        tiles[30].collision = true;

        // Tile 31 - tree 2
        tiles[31] = new Tile();
        tiles[31].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/grass/tree/t2.png")));
        tiles[31].collision = true;

        // Tile 32 - outward up mountain
        tiles[32] = new Tile();
        tiles[32].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("res/tiles/newTiles/ground/water/cliff/outward/up.png")));
    }


    // Load the map from a text file and parse it into the mapNumber array
    public void load(String path) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        assert is != null; // Ensure the file is found
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        int column = 0;
        int row = 0;

        // Read the map file line by line
        while (column < gamePanel.worldColumn && row < gamePanel.worldRow) {
            String line = br.readLine(); // Read one line from the file

            // Split the line into numbers and store them in the map array
            while (column < gamePanel.worldColumn) {
                String[] numbers = line.split(" ");
                int tileCode = Integer.parseInt(numbers[column]);
                mapNumber[column][row] = tileCode;
                if (tileCode==1||tileCode==2) {
                    collisionBounds[column][row] = new Rectangle(column * gamePanel.tileSize, row * gamePanel.tileSize, gamePanel.tileSize, gamePanel.tileSize);
                }
                column++;
            }

            // Move to the next row if we've reached the end of the current row
            if (column == gamePanel.worldColumn) {
                column = 0; // Reset the column
                row++; // Move to the next row
            }
        }

        br.close(); // Close the file reader
    }

    // Draw the map on the screen by iterating through the mapNumber array
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        // Loop through the entire map
        while (worldCol < gamePanel.worldColumn && worldRow < gamePanel.worldRow) {

            int tileNumber = mapNumber[worldCol][worldRow]; // Get the current tile number

            // Calculate the world position
            int worldX = worldCol * gamePanel.tileSize;
            int worldY = worldRow * gamePanel.tileSize;

            // Calculate the screen position based on the player's position
            int screenX = worldX - gamePanel.player.playerX + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.playerY + gamePanel.player.screenY;
            if (tileNumber==1||tileNumber==2){
                collisionBounds[worldCol][worldRow] = new Rectangle(screenX, screenY, gamePanel.tileSize, gamePanel.tileSize);
            }

            // Draw only tiles that are visible on the screen
            if (worldX + gamePanel.tileSize > gamePanel.player.playerX - gamePanel.player.screenX &&
                    worldX - gamePanel.tileSize < gamePanel.player.playerX + gamePanel.player.screenX &&
                    worldY + gamePanel.tileSize > gamePanel.player.playerY - gamePanel.player.screenY &&
                    worldY - gamePanel.tileSize < gamePanel.player.playerY + gamePanel.player.screenY) {
                // Draw the tile image at the calculated screen position
                g2.drawImage(tiles[tileNumber].image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
            }
            worldCol++;

            // If the end of the row is reached, reset x and move to the next row
            if (worldCol == gamePanel.worldColumn) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
