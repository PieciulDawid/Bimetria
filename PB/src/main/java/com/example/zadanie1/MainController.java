package com.example.zadanie1;

import com.example.zadanie1.components.CaptionedImageView;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import lombok.val;

import java.io.File;

public class MainController {
	
	@FXML
	private CaptionedImageView image;
	
	@FXML
	private ToggleButton global;
	
	@FXML
	private Slider slider;
	
	
	private EventHandler<MouseEvent> onImageClickedImpl =
			EventUtil.newSegmentOnClickHandler(30, false);
	
	private Image originalImage;
	
	private String imageURL;
	
	
	@FXML
	private void onImageClicked(MouseEvent e) {
		onImageClickedImpl.handle(e);
	}
	
	@FXML
	private void loadNewImage(ActionEvent e) {
		val chooser = new FileChooser();
		chooser.setTitle("Choose picture");
		
		File file = chooser.showOpenDialog(this.image.getScene().getWindow());
		
		if (file != null && file.canRead()) {
			imageURL = file.toURI().toString();
			originalImage = new Image(imageURL);
		}
		
		resetImageAndControlsState();
	}
	
	@FXML
	private void reset(ActionEvent e) {
		resetImageAndControlsState();
	}
	
	@FXML
	void initialize() {
		initImage();
		
		initParamChangedHandlers();
	}
	
	private void initImage() {
		imageURL = new File("PB/zdjecie2.jpeg").toURI().toString();
		
		originalImage = new Image(imageURL);
		image.setImage(originalImage);
		
		bindImageWidthToHeight();
	}
	
	private void initParamChangedHandlers() {
		global.selectedProperty().addListener(this::handleParamsChanged);
		slider.valueProperty().addListener(this::handleParamsChanged);
	}
	
	
	private void bindImageWidthToHeight() {
		val widthBinding = new DoubleBinding() {
			{
				super.bind(image.getImageView().imageProperty());
			}

			@Override
			protected double computeValue() {
				val img = image.getImageView().getImage();
				return image.getPrefImageHeight() * img.getWidth() / img.getHeight();
			}
		};
		
		image.prefImageWidthProperty().bind(widthBinding);
	}
	
	private void resetImageAndControlsState() {
		slider.setValue(30);
		global.setSelected(false);
		image.setImage(originalImage);
	}
	
	private void handleParamsChanged(Observable value) {
		onImageClickedImpl = EventUtil.newSegmentOnClickHandler(
				(int) slider.getValue(),
				global.isSelected());
	}
	
}
