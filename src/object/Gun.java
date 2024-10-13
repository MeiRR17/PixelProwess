//package object;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class Gun {
//    private BufferedImage rifleImage;
//
//    public Gun() {
//        try {
//            loadImage();
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle error, e.g., set rifleImage to a placeholder or log error
//        }
//    }
//
//    private void loadImage() throws IOException {
//        String imagePath = "/res/images/rifle.png"; // Ensure this path is correct
//        InputStream imgStream = getClass().getResourceAsStream(imagePath);
//        if (imgStream == null) {
//            throw new IOException("Image file not found: " + imagePath);
//        }
//        rifleImage = ImageIO.read(imgStream);
//    }
//
//    public void draw(Graphics g, int x, int y) {
//        if (rifleImage != null) {
//            g.drawImage(rifleImage, x, y, null);
//        } else {
//            System.err.println("Rifle image is not loaded.");
//        }
//    }
//}
