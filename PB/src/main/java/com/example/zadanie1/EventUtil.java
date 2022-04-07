package com.example.zadanie1;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.val;

public class EventUtil {
	public static EventHandler<MouseEvent> newOnPixelSelectedHandler(ImageAction action) {
		return (MouseEvent e) -> {
			if (!e.getButton().equals(MouseButton.PRIMARY)) {
				return;
			}
			
			if (!(e.getTarget() instanceof ImageView imageView)) {
				return;
			}
			
			final var image = imageView.getImage();
			final var h = image.getHeight() / imageView.getFitHeight();
			final var w = image.getWidth() / imageView.getFitWidth();
			
			final var x = e.getX() * w;
			final var y = e.getY() * h;
			
			
			action.apply(x, y, imageView);
		};
	}
	
	public static EventHandler<MouseEvent> newSegmentOnClickHandler(int tolerance, boolean global) {
		val seg = new Segmentation(0x0000_0000, tolerance, global);
		
		return newOnPixelSelectedHandler((x, y, imageView) -> {
			final var newImage = seg.apply(imageView.getImage(), (int) x, (int) y);
			
			imageView.setImage(newImage);
		});
	}
	
	@FunctionalInterface
	public interface ImageAction {
		void apply(double x, double y, ImageView imageView);
	}
}
