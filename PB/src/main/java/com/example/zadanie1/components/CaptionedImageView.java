package com.example.zadanie1.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;

public class CaptionedImageView extends VBox {
	@FXML
	private ImageView imageView;
	
	@FXML
	private Label label;
	
	@FXML
	private final DoubleProperty prefImageHeight = new SimpleDoubleProperty();
	
	@FXML
	private final DoubleProperty prefImageWidth = new SimpleDoubleProperty();
	
	@SneakyThrows
	public CaptionedImageView() {
		final var fxmlLoader = new FXMLLoader(getClass().getResource(
				"captioned-image-view.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		fxmlLoader.load();
	}
	
	public void setImage(Image image) {
		this.imageView.setImage(image);
	}
	
	public Image getImage() {
		return imageView.getImage();
	}
	
	public void setText(String value) {
		label.setText(value);
	}
	
	public String getText() {
		return label.getText();
	}
	
	public double getPrefImageHeight() {
		return prefImageHeight.get();
	}
	
	public DoubleProperty prefImageHeightProperty() {
		return prefImageHeight;
	}
	
	public void setPrefImageHeight(double prefImageHeight) {
		this.prefImageHeight.set(prefImageHeight);
	}
	
	public double getPrefImageWidth() {
		return prefImageWidth.get();
	}
	
	public DoubleProperty prefImageWidthProperty() {
		return prefImageWidth;
	}
	
	public void setPrefImageWidth(double prefImageWidth) {
		this.prefImageWidth.set(prefImageWidth);
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
	
	public void setOnMouseClickedOnImage(EventHandler<? super MouseEvent> value) {
		getImageView().setOnMouseClicked(value);
	}
}
