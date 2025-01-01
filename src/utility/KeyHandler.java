package utility;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean pressUp, pressDown, pressRight, pressLeft,
            isEPressed, pressReload, pressDropWeapon,
            pressSmallWeapon, pressBigWeapon;

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
            isEPressed = true;
        }
        // R for reload
        if (keyCode == KeyEvent.VK_R) {
            pressReload = true;
        }

        // Q for dropping items
        if(keyCode == KeyEvent.VK_Q){
            pressDropWeapon = true;
        }

        // 2 for picking small gun
        if(keyCode == KeyEvent.VK_2){
            pressSmallWeapon = true;
        }

        // 3 for picking big gun
        if(keyCode == KeyEvent.VK_3){
            pressBigWeapon  = true;
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
            isEPressed = false;
        }

        // R for reload
        if (keyCode == KeyEvent.VK_R) {
            pressReload = false;
        }

        // Q for dropping items
        if(keyCode == KeyEvent.VK_Q){
            pressDropWeapon = false;
        }

        // 2 for picking small gun
        if(keyCode == KeyEvent.VK_2){
            pressSmallWeapon = false;
        }

        // 3 for picking big gun
        if(keyCode == KeyEvent.VK_3){
            pressBigWeapon  = false;
        }
    }
}
