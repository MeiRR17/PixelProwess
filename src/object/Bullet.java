//package object;
//
//import main.GamePanel;
//import java.awt.*;
//
//public class Bullet {
//    private int x, y; // Position of the bullet
//    private int speed; // Speed of the bullet
//    private int direction; // Direction of the bullet
//    private boolean active; // Is the bullet active or not
//    public double distanceTravelled; // Distance traveled by the bullet
//
//    public Bullet(int startX, int startY, int direction, int speed) {
//        this.x = startX;
//        this.y = startY;
//        this.direction = direction;
//        this.speed = speed;
//        this.active = true;
//        this.distanceTravelled = 0; // Initialize distance
//    }
//
//    public void update() {
//        if (active) {
//            // Update bullet position based on direction
//            switch (direction) {
//                case 1: // Right
//                    x += speed;
//                    break;
//                case 2: // Down
//                    y += speed;
//                    break;
//                case 3: // Left
//                    x -= speed;
//                    break;
//                case 4: // Up
//                    y -= speed;
//                    break;
//            }
//
//            distanceTravelled += speed; // Increase distance traveled
//
//            // Deactivate bullet if it exceeds a certain distance (for example, 500 pixels)
//            if (distanceTravelled >= 500) {
//                active = false; // Mark as inactive
//            }
//        }
//    }
//
//    public boolean isActive() {
//        return active;
//    }
//
//    public void draw(Graphics2D g2) {
//        g2.setColor(Color.YELLOW); // Color for the bullet
//        g2.fillRect(x, y, 5, 5); // Draw the bullet as a rectangle
//    }
//}
