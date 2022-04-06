package com.example.zadanie1;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.function.BiConsumer;

public class EventUtil {
	public static EventHandler<MouseEvent> getPixelSelectionOnImageHandler(ImageAction action) {
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
	
	@FunctionalInterface
	public interface ImageAction {
		void apply(double x, double y, ImageView imageView);
	}
}
