package com.example.zadanie1.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

public class CaptionedImageView extends VBox {
	@FXML
	private ImageView image;
	
	@FXML
	private Label label;
	
	@SneakyThrows
	public CaptionedImageView() {
		final var fxmlLoader = new FXMLLoader(getClass().getResource("custom_control.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		fxmlLoader.load();
	}
	
	public void setImage(Image image) {
		this.image.setImage(image);
	}
	
	public Image getImage() {
		return image.getImage();
	}
	
	public void setText(String value) {
		label.setText(value);
	}
	
	public String getText() {
		return label.getText();
	}
}
