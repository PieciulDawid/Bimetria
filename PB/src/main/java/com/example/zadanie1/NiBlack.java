package com.example.zadanie1;

import java.awt.image.BufferedImage;

public class NiBlack {

    private static int width;
    private static int height;

    public static BufferedImage binarize(BufferedImage imageOrginal, double inputWindow) {

        width = imageOrginal.getWidth();
        height = imageOrginal.getHeight();

        int window = (int) inputWindow;

        final var img = ImageUtils.copyImage(imageOrginal);

        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                int acc = 0;
                for(int ji = -window ; ji < window ; ji++){
                    for(int jj = -window ; jj < window ; jj++){
                        if(column+ji >= 0 && column+ji < width)
                            if(row+jj >= 0 && row+jj < height)
                                acc += imageOrginal.getRGB(column+ji, row+jj) & 0x00ff0000 >> 16;
                    }
                }
                int pixel = img.getRGB(column, row) & 0x00ff0000 >> 16;
                if(pixel > acc / ((window*2) * (window*2)))
                    img.setRGB(column, row, 0x00FFFFFF);
                else
                    img.setRGB(column, row, 0x00000000);
            }
        }
        return img;
    }
}
