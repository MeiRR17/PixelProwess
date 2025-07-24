package main.manager;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {
    private Clip menuMusic;
    private Clip gameMusic;
    private FloatControl menuVolumeControl;
    private FloatControl gameVolumeControl;
    private boolean isMuted = false;
    private boolean isMusicEnabled = true;
    private boolean isAudioEnabled = true;

    public SoundManager() {
        initializeMusic();
    }
    public void setMusicEnabled(boolean enabled) {
        isMusicEnabled = enabled;
        if (!enabled) {
            stopMenuMusic();
            stopGameMusic();
        } else {
            // If we're enabling music, start the appropriate track
            // You might want to add a method to check which screen we're on
            startMenuMusic();
        }
    }
    public void setAudioEnabled(boolean enabled) {
        isAudioEnabled = enabled;
    }
    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }
    public boolean isAudioEnabled() {
        return isAudioEnabled && !isMuted;
    }

    private void initializeMusic() {
        try {
            // Initialize menu music
            InputStream menuMusicStream = getClass().getResourceAsStream("/audio/music/8-Bit Beat.wav");
            if (menuMusicStream != null) {
                BufferedInputStream bufferedMenuMusic = new BufferedInputStream(menuMusicStream);
                AudioInputStream menuAudioStream = AudioSystem.getAudioInputStream(bufferedMenuMusic);
                menuMusic = AudioSystem.getClip();
                menuMusic.open(menuAudioStream);
                menuVolumeControl = (FloatControl) menuMusic.getControl(FloatControl.Type.MASTER_GAIN);
            }

            // Initialize game music
            InputStream gameMusicStream = getClass().getResourceAsStream("/audio/music/Wick's Bounty.wav");
            if (gameMusicStream != null) {
                BufferedInputStream bufferedGameMusic = new BufferedInputStream(gameMusicStream);
                AudioInputStream gameAudioStream = AudioSystem.getAudioInputStream(bufferedGameMusic);
                gameMusic = AudioSystem.getClip();
                gameMusic.open(gameAudioStream);
                gameVolumeControl = (FloatControl) gameMusic.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void startMenuMusic() {
        if (menuMusic != null && !menuMusic.isRunning() && isMusicEnabled && !isMuted) {
            menuMusic.setFramePosition(0);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isRunning()) {
            menuMusic.stop();
        }
    }

    public void startGameMusic() {
        if (gameMusic != null && !gameMusic.isRunning() && isMusicEnabled && !isMuted) {
            gameMusic.setFramePosition(0);
            gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopGameMusic() {
        if (gameMusic != null && gameMusic.isRunning()) {
            gameMusic.stop();
        }
    }

    public void setVolume(float volume) {
        if (menuVolumeControl != null) {
            float range = menuVolumeControl.getMaximum() - menuVolumeControl.getMinimum();
            float gain = (range * volume) + menuVolumeControl.getMinimum();
            menuVolumeControl.setValue(gain);
        }
        if (gameVolumeControl != null) {
            float range = gameVolumeControl.getMaximum() - gameVolumeControl.getMinimum();
            float gain = (range * volume) + gameVolumeControl.getMinimum();
            gameVolumeControl.setValue(gain);
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            setVolume(0.0f);
        } else {
            setVolume(0.5f); // Default volume
        }
    }

    public void switchToGameMusic() {
        stopMenuMusic();
        // Only start game music if music is enabled
        if (isMusicEnabled && !isMuted) {
            startGameMusic();
        }
    }

    public void cleanup() {
        if (menuMusic != null) {
            menuMusic.close();
        }
        if (gameMusic != null) {
            gameMusic.close();
        }
    }
}