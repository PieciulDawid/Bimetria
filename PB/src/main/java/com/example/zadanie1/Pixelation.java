package com.example.zadanie1;

import java.awt.image.BufferedImage;

public class Pixelation {

    private static int width;
    private static int height;

    public static BufferedImage doPixelation(BufferedImage imageOrginal, double inputWindow){

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
}
