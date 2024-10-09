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

    // Constructor to initialize the TileManager
    public TileManager(GamePanel gamePanel) throws IOException {
        this.gamePanel = gamePanel;
        tiles = new Tile[10]; // Assume max 10 types of tiles
        mapNumber = new int[gamePanel.worldColumn][gamePanel.worldRow]; // Map data array

        getTileImage(); // Load the tile images
        load("/maps/map1.txt"); // Load the map layout
    }

    // Load tile images from the resources
    public void getTileImage() throws IOException {
        // Tile 0 - Grass
        tiles[0] = new Tile();
        tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/grass.png")));

        // Tile 1 - Wall
        tiles[1] = new Tile();
        tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/wall.png")));
        tiles[1].collision = true;

        // Tile 2 - Tree
        tiles[2] = new Tile();
        tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/tree.png")));
        tiles[2].collision = true;

        // Tile 3 - Earth
        tiles[3] = new Tile();
        tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/earth.png")));

        // Tile 4 - Water
        tiles[4] = new Tile();
        tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/water.png")));
        tiles[4].collision = true;

        // Tile 5 - Sand
        tiles[5] = new Tile();
        tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("tiles/sand.png")));
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
