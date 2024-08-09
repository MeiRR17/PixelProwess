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
    Tile[] tile;
    int[][] mapTileNum;

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        tile = new Tile[10];
        mapTileNum = new int[gamePanel.maxWorldColumn][gamePanel.maxWorldRow];
        getTileImage();
        loadMap("/maps/sampleMap.txt");
    }

    public void getTileImage() {

        try{

            tile[0] = new Tile();
            tile[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/grass.png")));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/wall.png")));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/water.png")));

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/earth.png")));

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/tree.png")));

            tile[5] = new Tile();
            tile[5].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/sand.png")));

            tile[6] = new Tile();
            tile[6].image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/tiles/tunnel_road.jpg")));



        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadMap(String path) {

        try {
            InputStream inputStream = getClass().getResourceAsStream(path);
            assert inputStream != null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            int column = 0;
            int row = 0;

            while (column < gamePanel.maxWorldColumn && row < gamePanel.maxWorldRow) {

                String line = bufferedReader.readLine();

                while (column < gamePanel.maxWorldColumn) {

                    String[] numbers = line.split(" ");

                    int num = Integer.parseInt(numbers[column]);

                    mapTileNum[column][row] = num;
                    column++;
                }
                if(column == gamePanel.maxWorldColumn) {
                    column = 0;
                    row++;
                }
            }
            bufferedReader.close();

        }catch (Exception ignored) {}
    }

    public void draw(Graphics2D g2){

        int worldColumn = 0;
        int worldRow = 0;

        while(worldColumn < gamePanel.maxWorldColumn && worldRow < gamePanel.maxWorldRow) {

            int tileNum = mapTileNum[worldColumn][worldRow];

            int worldX  = worldColumn * gamePanel.tileSize;
            int worldY  = worldRow * gamePanel.tileSize;
            int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

            g2.drawImage(tile[tileNum].image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
            worldColumn++;

            if(worldColumn == gamePanel.maxWorldColumn) {
                worldColumn = 0;
                worldRow++;
            }
        }
    }
}
