package com.example.zadanie1;

import java.awt.image.BufferedImage;

public class HistogramAlignUtil {

    private static int[] red = null;
    private static int[] green = null;
    private static int[] blue = null;

    public static BufferedImage alignHistogram(BufferedImage imageOrginal) {
        final var image = ImageUtils.copyImage(imageOrginal);

        init(image);
        //Tablice LUT dla skladowych
        int[] LUTred = calculateLUT(red, image.getWidth() * image.getHeight());
        int[] LUTgreen = calculateLUT(green, image.getWidth() * image.getHeight());
        int[] LUTblue = calculateLUT(blue, image.getWidth() * image.getHeight());

        for (int row = 0; row < image.getWidth(); row++) {
            for (int col = 0; col < image.getHeight(); col++) {
                int iRet = imageOrginal.getRGB(row, col);

                int iR = LUTred[(( iRet & 0xff0000) >> 16)];
                int iG = LUTgreen[(( iRet & 0x00ff00) >> 8)];
                int iB = LUTblue[(iRet & 0xff)];

                image.setRGB(row, col, (iR << 16) | (iG << 8) | iB);
            }
        }
        return image;
    }

    private static int[] calculateLUT(int[] values, int size) {
        double minValue = 0;
        for (int i = 0; i < 256; i++) {
            if (values[i] != 0) {
                minValue = values[i];
                break;
            }
        }

        int[] result = new int[256];
        double sum = 0;
        for (int i = 0; i < 256; i++){
            sum += values[i];
            result[i] = (int)(((sum - minValue) / (size - minValue)) * 255.0);
        }

        return result;
    }

    private static void init(BufferedImage image){
        //liczenie histogramu
        red = new int[256];
        green = new int[256];
        blue = new int[256];
        int iRet;
        int iB;
        int iG;
        int iR;
        int width = image.getWidth();
        int height = image.getHeight();

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                iRet = image.getRGB(row, col);
                iB = (iRet & 0xff);
                iG = (( iRet & 0x00ff00) >> 8);
                iR = (( iRet & 0xff0000) >> 16);

                ++green[iG];
                ++red[iR];
                ++blue[iB];
            }
        }
    }
}
