package game.entity.ui;

import java.awt.*;

public class UIManager {
    private static String currentPrompt = null;
    private static long promptStartTime = 0;
    private static final long PROMPT_DURATION = 2000; // 2 seconds

    public static void setPrompt(String prompt) {
        currentPrompt = prompt;
        promptStartTime = System.currentTimeMillis();
    }

    public static void clearPrompt() {
        currentPrompt = null;
    }

    public static void drawPrompt(Graphics2D g2, int x, int y) {
        if (currentPrompt != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - promptStartTime > PROMPT_DURATION) {
                clearPrompt();
                return;
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString(currentPrompt, x, y);
        }
    }
}