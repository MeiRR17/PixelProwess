//package main;
//
//import entity.Entity;
//
//import java.awt.*;
//
//public class UI {
//    GamePanel gamePanel;
//    Graphics2D g2;
//
//    public UI (GamePanel gamePanel) {
//        this.gamePanel = gamePanel;
//    }
//
////    public void inventory (Entity entity) {
////        // Creating a frame to store items
////        int x, y, width, height = 0;
////        int slot = 0;
////
////        if(entity == gamePanel.player) {
////
////        }
////    }
//
//    public void inventory() {
//        // frame
//        final int x = gamePanel.tileSize * 26;
//        final int y = gamePanel.tileSize * 16;
//        final int w = gamePanel.tileSize * 3;
//        final int h = gamePanel.tileSize;
//        drawInventory(x, y, w, h);
//
////        g2.drawImage(gamePanel.player.currentWeapon1, null);// need to put x,y
////        g2.drawImage(gamePanel.player.currentWeapon2, null);// need to put x,y
////        g2.drawImage(gamePanel.player.currentWeapon3, null);// need to put x,y
//    }
//
//    public void drawInventory(int x, int y, int w, int h) {
//        Color c = new Color(0,0,0,210);  // R,G,B, alfa(opacity)
//        g2.setColor(c);
//        g2.fillRoundRect(x,y,w,h,35,35);
//
//        c = new Color(255,255,255);
//        g2.setColor(c);
//        g2.setStroke(new BasicStroke(5));    // 5 = width of outlines of graphics
//        g2.drawRoundRect(x+5,y+5,w-10,h-10,25,25);
//    }
//}
