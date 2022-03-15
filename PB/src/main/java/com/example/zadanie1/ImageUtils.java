package com.example.zadanie1;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class ImageUtils {
	public static BufferedImage copyImage(BufferedImage source) {
		var props = HelloApplication.getPropsFromImage(source);
		return HelloApplication.copyImage(source, props);
	}
}
