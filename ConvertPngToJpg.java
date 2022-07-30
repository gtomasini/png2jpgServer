//package com.memorynotfound.image;

//This example demonstrates how to convert PNG to JPG image file using Java. You can expect some quality loss 
//when converting images to/from PNG/JPG. PNG supports transparent background. JPG does not support transparent background.
//We can overcome this limitation by replacing the transparent background with a default background color.

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConvertPngToJpg {

    public static void main(String... args) {

        try {
            File input = new File("sample.png");
            File output = new File("sample-to-jpg.jpg");

            BufferedImage image = ImageIO.read(input);
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            //result.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
			//not transparent, PINK
			result.createGraphics().drawImage(image, 0, 0, Color.PINK, null);
            ImageIO.write(result, "jpg", output);

        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

}