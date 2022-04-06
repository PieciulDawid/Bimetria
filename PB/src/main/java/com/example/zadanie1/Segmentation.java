package com.example.zadanie1;

import javafx.scene.image.Image;

public class Segmentation {
	
	public static Image apply(Image image, int x, int y, int tolerance) {
		final int width = (int) image.getWidth();
		final int height = (int) image.getHeight();
		
		final int[] data = ImageUtils.getBinaryDataFrom(image);
		
		final int pixel = data[x + y * width];
		final int R = pixel & 0xFF0000 >> 16;
		final int G = pixel & 0xFF00 >> 8;
		final int B = pixel & 0xFF;
		
		final int minR = R - tolerance, maxR = R + tolerance;
		final int minG = G - tolerance, maxG = G + tolerance;
		final int minB = B - tolerance, maxB = B + tolerance;
		final boolean[][] visited = new boolean[height][width];
		recurrentFill(height, width, x, y, minR, minG, minB, maxR, maxG, maxB, data, visited);
		
		return ImageUtils.createImage(width, height, data);
	}
	
	private static void recurrentFill(
			int height, int width,
			int x, int y,
			int minR, int minG, int minB,
			int maxR, int maxG, int maxB,
			int[] data,
			boolean[][] visited) {
		
		visited[y][x] = true;
		
		final int pixel = data[x + y * width];
		final int R = pixel & 0xFF0000 >> 16;
		final int G = pixel & 0xFF00 >> 8;
		final int B = pixel & 0xFF;
		
		if (R > maxR || R < minR || G > maxG || G < minG || B > maxB || B < minB) {
			return;
		}
		
		data[x + y * width] = 0x0000_0000;
		
		if (y - 1 >= 0 && !visited[y - 1][x]) {
			recurrentFill(height, width, x, y - 1, minR, minG, minB, maxR, maxG, maxB, data, visited);
		}
		if (x - 1 >= 0 && !visited[y][x - 1]) {
			recurrentFill(height, width, x - 1, y, minR, minG, minB, maxR, maxG, maxB, data, visited);
		}
		if (x + 1 < width && !visited[y][x + 1]) {
			recurrentFill(height, width, x + 1, y, minR, minG, minB, maxR, maxG, maxB, data, visited);
		}
		if (y + 1 < height && !visited[y + 1][x]) {
			recurrentFill(height, width, x, y + 1, minR, minG, minB, maxR, maxG, maxB, data, visited);
		}
	}
	
}
