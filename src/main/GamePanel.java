package main;

import entity.Player;
import object.ObjMaster;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    //screen settings
    public final int orgTileSize = 32; //32x32 tile
    final int scale = 2; //scaling the tile to make it bigger

    public final int tileSize = orgTileSize * scale;
    public final int maxScreenColumn = 30;
    public final int maxScreenRow = 18;
    public final int screenWidth = tileSize * maxScreenColumn;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int worldColumn = 50;
    public final int worldRow = 50;

    int FPS = 60;
    TileManager tileManager = new TileManager(this);

    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    public Collision collisionCheck = new Collision(this);
    public ObjectPlacer objectPlacer = new ObjectPlacer(this);
    public Player player = new Player(this, keyHandler);
    public ObjMaster[] objMaster = new ObjMaster[25];

    public GamePanel() throws IOException {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set the wanted size of the panel
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //set this component to be double buffered
        this.addKeyListener(keyHandler);
        this.setFocusable(true); //make the gamePanel receive key input

    }

    public void gameSet() throws IOException {
        objectPlacer.placeObjects();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double interval = (double) 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + interval;


        while (gameThread != null) {

            // UPDATE : update information
            update();
            //DRAW : draw the screen (basically the FPS of the game)
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += interval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() {

        player.update();

    }

    //To draw something on the screen
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //layer1
        tileManager.draw(g2);
        //layer2
        for (ObjMaster master : objMaster) {
            if (master != null) {
                master.draw(g2, this);
            }
        }
            //layer3
            player.draw(g2);
            g2.dispose(); //dispose of this graphics context and release any system resources
    }
}