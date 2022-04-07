package com.example.zadanie1;


import javafx.scene.image.Image;
import lombok.Getter;

import java.util.Arrays;
import java.util.PrimitiveIterator;

@Getter
public class ProcessableImage implements Iterable<Integer> {
	
	private final int[] data;
	
	private final int height;
	
	private final int width;
	
	
	private ProcessableImage(Image image) {
		width = (int) image.getWidth();
		height = (int) image.getHeight();
		
		data = ImageUtils.getBinaryDataFrom(image, width, height);
	}
	
	public static ProcessableImage formImage(Image image) {
		return new ProcessableImage(image);
	}
	
	public Image intoImage() {
		return ImageUtils.createImage(width, height, data);
	}
	
	public int pixelAt(Point point) {
		return data[point.getX() + point.getY() * width];
	}
	
	public void setPixelAt(Point point, int value) {
		data[point.getX() + point.getY() * width] = value;
	}
	
	@Override
	public PrimitiveIterator.OfInt iterator() {
		return Arrays.stream(data).iterator();
	}
	
}
