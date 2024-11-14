package utility;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean pressUp, pressDown, pressRight, pressLeft, pressSpace, pressReload;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used, but required by KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // W for up
        if (keyCode == KeyEvent.VK_W) {
            pressUp = true;
        }

        // S for down
        if (keyCode == KeyEvent.VK_S) {
            pressDown = true;
        }

        // D for right
        if (keyCode == KeyEvent.VK_D) {
            pressRight = true;
        }

        // A for left
        if (keyCode == KeyEvent.VK_A) {
            pressLeft = true;
        }

        // E for picking up items
        if (keyCode == KeyEvent.VK_E) {
            pressSpace = true;
        }
        if (keyCode == KeyEvent.VK_R) {
            pressReload = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // W for up
        if (keyCode == KeyEvent.VK_W) {
            pressUp = false;
        }

        // S for down
        if (keyCode == KeyEvent.VK_S) {
            pressDown = false;
        }

        // D for right
        if (keyCode == KeyEvent.VK_D) {
            pressRight = false;
        }

        // A for left
        if (keyCode == KeyEvent.VK_A) {
            pressLeft = false;
        }

        // E for picking up items
        if (keyCode == KeyEvent.VK_E) {
            pressSpace = false;
        }

        if (keyCode == KeyEvent.VK_R) {
            pressReload = false;
        }
    }
}
