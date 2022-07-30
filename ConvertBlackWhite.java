//package com.memorynotfound.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import sun.misc.BASE64Encoder;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class ConvertBlackWhite {
	static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
 
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
 
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
	
    public static void main(String... args) {

        try {
            File input = new File("zebra-logo.png");
            BufferedImage image = ImageIO.read(input);
			String base64=encodeToString(image, "png");
			System.out.println(base64);
			PrintWriter out = new PrintWriter("base64.txt");
			out.print(base64);
			out.close();
			
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_BYTE_BINARY);

            //Graphics2D graphic = result.createGraphics();
            //graphic.drawImage(image, 0, 0, Color.WHITE, null);
            //graphic.dispose();

            //File output = new File("out.png");
            //ImageIO.write(result, "png", output);

        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

}