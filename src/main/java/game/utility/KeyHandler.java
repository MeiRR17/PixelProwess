package game.utility;

import main.menu.GameState;
import main.menu.OptionsMenu;

import javax.sound.sampled.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class KeyHandler implements KeyListener {
    // Key states
    public boolean pressUp, pressDown, pressRight, pressLeft,
            isEPressed, pressReload, pressDropWeapon,
            pressSmallWeapon, pressBigWeapon,
            pressUsePotion;

    private GameState gameState;
    private Clip walkingSound;
    private boolean isWalkingSoundPlaying = false;
    private long lastWalkingSoundTime = 0;
    private static final long WALKING_SOUND_DELAY = 500; // Delay between walking sound loops

    public KeyHandler(GameState gameState) {
        this.gameState = gameState;
        initializeWalkingSound();
    }

    private void initializeWalkingSound() {
        try {
            AudioInputStream walkingAudioStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/audio/sound effect/walking.wav"));
            walkingSound = AudioSystem.getClip();
            walkingSound.open(walkingAudioStream);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | NullPointerException e) {
            System.out.println("Error loading walking sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playWalkingSound() {
        if (OptionsMenu.isAudioEnabled() && walkingSound != null && !isWalkingSoundPlaying) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastWalkingSoundTime >= WALKING_SOUND_DELAY) {
                walkingSound.setFramePosition(0);
                walkingSound.start();
                isWalkingSoundPlaying = true;
                lastWalkingSoundTime = currentTime;
            }
        }
    }

    private void stopWalkingSound() {
        if (walkingSound != null && isWalkingSoundPlaying) {
            walkingSound.stop();
            isWalkingSoundPlaying = false;
        }
    }

    private boolean isAnyMovementKeyPressed() {
        return pressUp || pressDown || pressLeft || pressRight;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used but required by KeyListener interface
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Movement keys
        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                pressUp = true;
                if (OptionsMenu.isAudioEnabled()) playWalkingSound();
                break;
            case KeyEvent.VK_Q:
                pressDropWeapon = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                pressDown = true;
                if (OptionsMenu.isAudioEnabled()) playWalkingSound();
                break;

            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                pressLeft = true;
                if (OptionsMenu.isAudioEnabled()) playWalkingSound();
                break;

            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                pressRight = true;
                if (OptionsMenu.isAudioEnabled()) playWalkingSound();
                break;

            // Action keys
            case KeyEvent.VK_E:
                isEPressed = true;
                break;

            case KeyEvent.VK_R:
                pressReload = true;
                break;

            case KeyEvent.VK_G:
                pressDropWeapon = true;
                break;

            case KeyEvent.VK_1:
                pressBigWeapon = true;
                break;

            case KeyEvent.VK_2:
                pressSmallWeapon = true;
                break;

            case KeyEvent.VK_3:
                pressUsePotion = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                pressUp = false;
                if (!isAnyMovementKeyPressed()) stopWalkingSound();
                break;
            case KeyEvent.VK_Q:
                pressDropWeapon = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                pressDown = false;
                if (!isAnyMovementKeyPressed()) stopWalkingSound();
                break;

            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                pressLeft = false;
                if (!isAnyMovementKeyPressed()) stopWalkingSound();
                break;

            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                pressRight = false;
                if (!isAnyMovementKeyPressed()) stopWalkingSound();
                break;

            case KeyEvent.VK_E:
                isEPressed = false;
                break;

            case KeyEvent.VK_R:
                pressReload = false;
                break;

            case KeyEvent.VK_G:
                pressDropWeapon = false;
                break;

            case KeyEvent.VK_1:
                pressBigWeapon = false;
                break;

            case KeyEvent.VK_2:
                pressSmallWeapon = false;
                break;

            case KeyEvent.VK_3:
                pressUsePotion = false;
                break;

        }
    }

    public void cleanup() {
        if (walkingSound != null) {
            walkingSound.close();
        }
    }
}
