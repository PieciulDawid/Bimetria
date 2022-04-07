package com.example.zadanie1;

import javafx.scene.image.*;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Hashtable;

public class ImageUtils {
	public static BufferedImage copyImage(BufferedImage source) {
		var props = getPropsFromImage(source);
		return copyImage(source, props);
	}
	
	public static int[] getBinaryDataFrom(Image image) {
		final int width = (int) image.getWidth();
		final int height = (int) image.getHeight();
		
		return getBinaryDataFrom(image, width, height);
	}
	
	public static int[] getBinaryDataFrom(Image image, int width, int height) {
		final int[] data = new int[width * height];
		
		image.getPixelReader().getPixels(
				0, 0, width, height, PixelFormat.getIntArgbPreInstance(), data, 0, width);
		
		return data;
	}
	
	public static WritableImage createImage(int width, int height, int[] imageData) {
		final var buffer = new PixelBuffer<>(width, height,
				IntBuffer.wrap(imageData),
				PixelFormat.getIntArgbPreInstance());
		
		return new WritableImage(buffer);
	}
	
	public static Image convertToFxImage(BufferedImage image) {
		WritableImage wr = null;
		if (image != null) {
			wr = new WritableImage(image.getWidth(), image.getHeight());
			PixelWriter pw = wr.getPixelWriter();
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					pw.setArgb(x, y, image.getRGB(x, y));
				}
			}
		}
		return wr;
	}
	
	public static BufferedImage convertToSwingImage(Image image) {
		BufferedImage wr = null;
		if (image != null) {
			final int width = (int) image.getWidth();
			final int height = (int) image.getHeight();
			wr = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
			
			final var pixelReader = image.getPixelReader();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					wr.setRGB(x, y, pixelReader.getArgb(x, y));
				}
			}
		}
		return wr;
	}
	
	public static BufferedImage copyImage(BufferedImage source, Hashtable<String, Object> copiedProps) {
		return new BufferedImage(
				source.getColorModel(),
				source.copyData(null),
				false, copiedProps);
	}
	
	public static Hashtable<String, Object> getPropsFromImage(BufferedImage source) {
		final var copiedProps = new Hashtable<String, Object>();
		if (source.getPropertyNames() != null) {
			Arrays.stream(source.getPropertyNames()).forEachOrdered((key) -> {
				copiedProps.put(key, source.getProperty(key));
			});
		}
		return copiedProps;
	}
	
}
