package dev.mvc.tool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/**
 * jpg로 강제 변환하는 메서드
 * @author i
 *
 */
public class ImageConverter {
    public static File convertToJPG(String imageUrl, String outputPath) throws Exception {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        File outputFile = new File(outputPath);
        ImageIO.write(image, "jpg", outputFile);
        return outputFile;
    }
}
