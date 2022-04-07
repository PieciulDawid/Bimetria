package com.example.zadanie1;

import javafx.scene.image.Image;
import lombok.val;

import java.util.ArrayDeque;
import java.util.function.BiConsumer;

public class Segmentation {

	private final int fill;
	
	private final int tolerance;
	
	private final BiConsumer<Point, ProcessableImage> fillImpl;
	
	private int minR;
	private int minG;
	private int minB;
	
	private int maxR;
	private int maxG;
	private int maxB;
	
	public Segmentation(int fill, int tolerance, boolean global) {
		this.fill = fill;
		this.tolerance = tolerance;
		this.fillImpl = global ? this::globalFill : this::localFill;
	}
	
	
	public Image apply(Image image, int x, int y) {
		val img = ProcessableImage.formImage(image);
		
		fillImpl.accept(Point.of(x, y), img);

		return img.intoImage();
	}
	
	
	private void globalFill(Point start, ProcessableImage image) {
		initRanges(start, image);
		
		val data = image.getData();
		val dataLen = data.length;
		
		
		for (int i = 0; i < dataLen; i++) {
			if (isOutsideToleranceRange(data, i)) {
				continue;
			}
			
			data[i] = fill;
		}
	}
	
	private void localFill(Point start, ProcessableImage image) {
		initRanges(start, image);
		
		val height = image.getHeight();
		val width = image.getWidth();

		
		val visited = new boolean[height][width];
		val toBeColored = new ArrayDeque<Point>();

		
		toBeColored.add(start);
		
		visited[start.getY()][start.getX()] = true;
		
		
		while (! toBeColored.isEmpty()) {
			Point p = toBeColored.poll();
			
			val x = p.getX();
			val y = p.getY();


			if (isOutsideToleranceRange(image, p)) {
				continue;
			}

			image.setPixelAt(p, fill);


			if (x - 1 >= 0 && !visited[y][x - 1]) {
				visited[y][x - 1] = true;
				toBeColored.add(p.translatedBy(-1, 0));
			}
			if (y - 1 >= 0 && !visited[y - 1][x]) {
				visited[y - 1][x] = true;
				toBeColored.add(p.translatedBy(0, -1));
			}
			if (x + 1 < width && !visited[y][x + 1]) {
				visited[y][x + 1] = true;
				toBeColored.add(p.translatedBy(1, 0));
			}
			if (y + 1 < height && !visited[y + 1][x]) {
				visited[y + 1][x] = true;
				toBeColored.add(p.translatedBy(0, 1));
			}
		}

	}
	
	
	private void initRanges(Point sourcePoint, ProcessableImage image) {
		val pixel = image.pixelAt(sourcePoint);
		
		val R = pixel & 0xFF0000 >> 16;
		val G = pixel & 0xFF00 >> 8;
		val B = pixel & 0xFF;
		
		minR = R - tolerance;
		minG = G - tolerance;
		minB = B - tolerance;
		
		maxR = R + tolerance;
		maxG = G + tolerance;
		maxB = B + tolerance;
	}
	
	
	private boolean isOutsideToleranceRange(ProcessableImage image, Point p) {
		val currPixel = image.pixelAt(p);
		
		return isOutsideToleranceRangeImpl(currPixel);
	}
	
	private boolean isOutsideToleranceRange(int[] data, int i) {
		val currPixel = data[i];
		
		return isOutsideToleranceRangeImpl(currPixel);
	}
	
	
	private boolean isOutsideToleranceRangeImpl(int currPixel) {
		val currR = currPixel & 0xFF0000 >> 16;
		val currG = currPixel & 0xFF00 >> 8;
		val currB = currPixel & 0xFF;
		
		return currR > maxR || currR < minR || currG > maxG || currG < minG || currB > maxB || currB < minB;
	}
	
}
