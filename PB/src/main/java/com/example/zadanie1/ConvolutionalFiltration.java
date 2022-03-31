package com.example.zadanie1;

import javafx.scene.image.Image;

public class ConvolutionalFiltration {
	public static int counter1;
	public static int counter2;
	
	/**
	 * Convolves specified data with supplied mask while treating it like a matrix
	 * {@code width} wide and {@code height} high. Returned {@code int[]} contains
	 * results of the convolution in raw form -- not pixel values.
	 *
	 * @param imageData image's data in ARGB format
	 * @param width image's width
	 * @param height image's height
	 * @param mask mask as square matrix
	 * @return image's data convoluted with mask
	 */
	public static int[] convolve(int[] imageData, int width, int height, int[][] mask) {
		
		final int[] convolvedData = new int[imageData.length];

		final int dataLen = imageData.length;
		final int maskReach = mask.length / 2;
		final int maskWidth = mask.length;

		
		for (int currRowOffset = 0; currRowOffset < dataLen; currRowOffset += width) {
			for (int col = 0; col < width; col++) {
				
				int acc = 0;
				for (int maskI = 0; maskI < maskWidth; maskI++) {
					for (int maskJ = 0; maskJ < maskWidth; maskJ++) {
						
						final int dataIndex = getDataIndex(height, maskReach, col, currRowOffset, maskI, maskJ);
						
						if (dataIndex < 0 || dataIndex >= dataLen) {
							continue;
						}
						
						
						final int iRet = imageData[dataIndex];
						
						final int iR = (( iRet & 0xff0000) >> 16);
						final int iG = (( iRet & 0x00ff00) >> 8);
						final int iB = (iRet & 0xff);
						
						final int iAvg = (iR + iG + iB) / 3;
						
						acc += mask[maskI][maskJ] * iAvg;
					}
				}
				
				convolvedData[currRowOffset + col] = acc;
			}
		}
		
		return convolvedData;
	}
	
	public static Image apply(Image image, MaskType matrixType) {
		final int width = (int) image.getWidth();
		final int height = (int) image.getHeight();
		
		final int[] data = ImageUtils.getBinaryDataFrom(image);
		final int dataLen = data.length;
		
		
		final int[] resultA = convolve(data, width, height, matrixType.getA());
		final int[] resultB = convolve(data, width, height, matrixType.getB());
		
		final int[] resultData = new int[dataLen];
		
		
		for (int i = 0; i < dataLen; i++) {
			final double squaredA = Math.pow(resultA[i], 2);
			final double squaredB = Math.pow(resultB[i], 2);
			
			final int singleColor = ((int) Math.sqrt(squaredA + squaredB)) & 0xff;
			
			resultData[i] = 0xff00_0000 | (singleColor << 16) | (singleColor << 8) | singleColor;
		}
		
		
		return ImageUtils.createImage(width, height, resultData);
	}
	
	private static int getDataIndex(int height, int maskReach, int col, int currRowOffset, int maskI, int maskJ) {
		return maskJ - maskReach + col + (maskI - maskReach) * height + currRowOffset;
	}
	
}
