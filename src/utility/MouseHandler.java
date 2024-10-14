package utility;

import entity.Player;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private Player player;
    private boolean shooting = false;

    public MouseHandler(Player player) {
        this.player = player;
    }

    // Handle mouse movement (required for MouseMotionListener)
    @Override
    public void mouseMoved(MouseEvent e) {
        player.updateAngle(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        player.updateAngle(e.getX(), e.getY());
    }

    // Handle mouse events (required for MouseListener)
    @Override
    public void mouseClicked(MouseEvent e) {
        // Trigger shooting when the mouse is clicked
        try {
            player.shootBullet();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
            shooting = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
            shooting = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Handle if needed (e.g., cursor enters the game area)
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Handle if needed (e.g., cursor exits the game area)
    }

    public boolean isShooting() {
        return shooting;
    }
}
