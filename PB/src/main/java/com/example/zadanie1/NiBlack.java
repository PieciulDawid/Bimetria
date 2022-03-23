package com.example.zadanie1;

import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

public class NiBlack {

    private static int width;
    private static int height;

    public static BufferedImage apply(
            BufferedImage imageOrginal,
            double inputWindow,
            DoubleBinaryOperator calculateLocalThreshold) {

        width = imageOrginal.getWidth();
        height = imageOrginal.getHeight();

        int window = (int) inputWindow;

        final var img = ImageUtils.copyImage(imageOrginal);

        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                int iB = 0;
                int iG = 0;
                int iR = 0;
                int sum;
                int rgb;
                for (int ji = -window; ji < window; ji++) {
                    for (int jj = -window; jj < window; jj++) {
                        if (column + ji >= 0 && column + ji < width) {
                            if (row + jj >= 0 && row + jj < height) {
                                rgb = imageOrginal.getRGB(column + ji, row + jj);
                                iR += rgb & 0xff0000 >> 16;
                                iG += rgb & 0x00ff00 >> 8;
                                iB += rgb & 0xff;
                            }
                        }
                    }
                }
                sum = (iR + iG + iB) / 3;
                double area = (window * 2) * (window * 2);
                double variance = 0.0;
                double mean = sum / area;
                int num;
                for (int ji = -window; ji < window; ji++) {
                    for (int jj = -window; jj < window; jj++) {
                        if (column + ji >= 0 && column + ji < width) {
                            if (row + jj >= 0 && row + jj < height) {
                                rgb = imageOrginal.getRGB(column + ji, row + jj);
                                iR = rgb & 0xff0000 >> 16;
                                iG = rgb & 0x00ff00 >> 8;
                                iB = rgb & 0xff;
                                num = (iR + iG + iB) / 3;
                                variance += Math.pow(num - mean, 2);
                            }
                        }
                    }
                }
                double SD = Math.sqrt(variance / area);
                int pixelRgb = img.getRGB(column, row);
                int pixelR = pixelRgb & 0xff0000 >> 16;
                int pixelG = pixelRgb & 0x00ff00 >> 8;
                int pixelB = pixelRgb & 0xff;
                int pixelA = (pixelR + pixelG + pixelB) / 3;
                double average = sum / area;
    
                //***********************************//
    
                //Wzór NiBlaca
                double NiBlack = calculateLocalThreshold.applyAsDouble(SD, average);
    
                //***********************************//
                if (pixelA > NiBlack)
                    img.setRGB(column, row, 0xffffff);
                else
                    img.setRGB(column, row, 0x000000);
            }
        }
        return img;
    }
}
