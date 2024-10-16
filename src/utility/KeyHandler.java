package utility;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean pressUp, pressDown, pressRight, pressLeft, pressSpace;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used, but required by KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // W for up
        if(keyCode == KeyEvent.VK_W) {
            pressUp = true;
        }

        // S for down
        if(keyCode == KeyEvent.VK_S) {
            pressDown = true;
        }

        // D for right
        if(keyCode == KeyEvent.VK_D) {
            pressRight = true;
        }

        // A for left
        if(keyCode == KeyEvent.VK_A) {
            pressLeft = true;
        }

        // Space for shooting
        if(keyCode == KeyEvent.VK_SPACE) {
            pressSpace = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // W for up
        if(keyCode == KeyEvent.VK_W) {
            pressUp = false;
        }

        // S for down
        if(keyCode == KeyEvent.VK_S) {
            pressDown = false;
        }

        // D for right
        if(keyCode == KeyEvent.VK_D) {
            pressRight = false;
        }

        // A for left
        if(keyCode == KeyEvent.VK_A) {
            pressLeft = false;
        }

        // Space for shooting
        if(keyCode == KeyEvent.VK_SPACE) {
            pressSpace = false;
        }
    }
}
