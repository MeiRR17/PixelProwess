package game.utility;

import java.awt.*;

public class MouseInfoUtil {
    public static Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    // Method to get the mouse X position
    public static int getMouseX() {
        return (int) getMousePosition().getX();
    }

    // Method to get the mouse Y position
    public static int getMouseY() {
        return (int) getMousePosition().getY();
    }
}
