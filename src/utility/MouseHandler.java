package utility;

import entity.Player;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    private Player player;
    public boolean shooting = false;

    public MouseHandler(Player player) {
        this.player = player;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        player.updateAngle(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        player.updateAngle(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
            shooting = true; // Set shooting to true when button is pressed
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
            shooting = false; // Reset shooting when button is released
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    public boolean isShooting() {
        return shooting;
    }
}