package com.example.zadanie1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Median {

    private static int width;
    private static int height;

    public static BufferedImage doMedian(BufferedImage imageOrginal, double inputWindow){

        width = imageOrginal.getWidth();
        height = imageOrginal.getHeight();

        int window = (int) inputWindow;
        final var img = ImageUtils.copyImage(imageOrginal);

        int rgb;
        int[] R;
        int[] B;
        int[] G;
        int median;
        List<Color> windowPixels = new ArrayList<>();
        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                for (int ji = -window; ji < window; ji++) {
                    for (int jj = -window; jj < window; jj++) {
                        if (column + ji >= 0 && column + ji < width) {
                            if (row + jj >= 0 && row + jj < height) {
                                rgb = imageOrginal.getRGB(column + ji, row + jj);
                                windowPixels.add(new Color(rgb));
                            }
                        }
                    }
                }
                R = new int[windowPixels.size()];
                B = new int[windowPixels.size()];
                G = new int[windowPixels.size()];

                for(int k = 0;k<windowPixels.size();k++){
                    R[k] = windowPixels.get(k).getRed();
                    B[k] = windowPixels.get(k).getBlue();
                    G[k] = windowPixels.get(k).getGreen();
                }
                Arrays.sort(R);
                Arrays.sort(G);
                Arrays.sort(B);
                median = windowPixels.size()/2;
                img.setRGB(column,row,new Color(R[median],G[median],B[median]).getRGB());
                windowPixels.clear();
            }
        }
        return img;
    }
}