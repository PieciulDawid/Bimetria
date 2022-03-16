package com.example.zadanie1;

import java.awt.image.BufferedImage;
import java.nio.channels.FileChannel;

public class HistogramStretchUtil {
	
	private HistogramStretchUtil() {}
	
	public static final int MIN = 0;
	public static final int MAX = 1;
	
	public static final int R = 0;
	public static final int G = 1;
	public static final int B = 2;
	
	/**
	 * Finds channel-wise min and max search for the whole image.
	 *
	 * @param image image to use
	 * @return array with following layout:
	 * <table>
	 *     <tr>
	 * 	       <td></td>|
	 * 	       <td>red</td>|
	 * 	       <td>green</td>|
	 * 	       <td>blue</td>
	 * 	   </tr>
	 * 	   <tr>
	 * 	       <td>min</td>|
	 * 	       <td>[R][MIN]</td>|
	 * 	       <td>[G][MIN]</td>|
	 * 	       <td>[B][MIN]</td>
	 * 	   </tr>
	 * 	   <tr>
	 * 	       <td>max</td>|
	 * 	       <td>[R][MAX]</td>|
	 * 	       <td>[G][MAX]</td>|
	 * 	       <td>[B][MAX]</td>
	 * 	   </tr>
	 * </table>
	 */
	public static int[][] minMaxChannelWise(BufferedImage image) {
		var redMinMax = new int[] {255, 0};
		var greenMinMax = new int[] {255, 0};
		var blueMinMax = new int[] {255, 0};
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				int iRet = image.getRGB(row, col);
				
				
				int iR = (( iRet & 0xff0000) >> 16);
				int iG = (( iRet & 0x00ff00) >> 8);
				int iB = (iRet & 0xff);
				
				redMinMax[MIN] = Math.min(iR, redMinMax[MIN]);
				greenMinMax[MIN] = Math.min(iG, greenMinMax[MIN]);
				blueMinMax[MIN] = Math.min(iB, blueMinMax[MIN]);
				
				redMinMax[MAX] = Math.max(iR, redMinMax[MAX]);
				greenMinMax[MAX] = Math.max(iG, greenMinMax[MAX]);
				blueMinMax[MAX] = Math.max(iB, blueMinMax[MAX]);
			}
		}
		
		return new int[][] {redMinMax, greenMinMax, blueMinMax};
	}
	
	public static BufferedImage stretchHistogram(BufferedImage image) {
		final var copy = ImageUtils.copyImage(image);
		
		var minMax = minMaxChannelWise(copy);
		
		var params = new double[] {
				255 / ((double) (minMax[R][MAX] - minMax[R][MIN])),
				255 / ((double) (minMax[G][MAX] - minMax[G][MIN])),
				255 / ((double) (minMax[B][MAX] - minMax[B][MIN]))
		};
		
		int width = copy.getWidth();
		int height = copy.getHeight();
		
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				int iRet = copy.getRGB(row, col);
				
				
				int iR = (( iRet & 0xff0000) >> 16);
				int iG = (( iRet & 0x00ff00) >> 8);
				int iB = (iRet & 0xff);
				
				iR = (int) (params[R] * (iR - minMax[R][MIN]));
				iG = (int) (params[G] * (iG - minMax[G][MIN]));
				iB = (int) (params[B] * (iB - minMax[B][MIN]));
				
				copy.setRGB(row, col, (iR << 16) | (iG << 8) | iB);
			}
		}
		
		return copy;
	}
}
