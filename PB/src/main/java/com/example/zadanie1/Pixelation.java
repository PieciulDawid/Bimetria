package com.example.zadanie1;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class Pixelation {

    private static int width;
    private static int height;

    public static BufferedImage apply(BufferedImage imageOrginal, double inputWindow){

        width = imageOrginal.getWidth();
        height = imageOrginal.getHeight();

        int window = (int) inputWindow;
        final var img = ImageUtils.copyImage(imageOrginal);
        int pixel;

        for(int column = 0; column < height; column += window) {
            for(int row = 0; row < width; row += window) {
                pixel = imageOrginal.getRGB(row, column);
                for(int ji = column; (ji < column + window) && (ji < height); ji++) {
                    for(int jj = row; (jj < row + window) && (jj < width); jj++) {
                        img.setRGB(jj, ji, pixel);
                    }
                }
            }
        }
        return img;
    }
    
    public static Image apply(Image image, double inputWindow) {
    
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
    
        final int[] data = ImageUtils.getBinaryDataFrom(image);
        final int dataLen = data.length;
    
        final int window = (int) inputWindow;
        
        
        for (int rowOffset = 0; rowOffset < dataLen; rowOffset += window * width) {
            for (int col = 0; col < width; col += window) {
                final int repeatedPixel = data[rowOffset + col];
                
                for (int ji = rowOffset; (ji < rowOffset + window * width) && (ji < dataLen); ji += width) {
                    for (int jj = col; (jj < col + window) && (jj < width); jj++) {
                        data[ji + jj] = repeatedPixel;
                    }
                }
            }
        }
        
        return ImageUtils.createImage(width, height, data);
    }
}
