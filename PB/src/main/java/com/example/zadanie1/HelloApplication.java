package com.example.zadanie1;

import com.example.zadanie1.components.CaptionedImageView;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import lombok.SneakyThrows;

public class HelloApplication extends Application {

    private static int[] histogramAverageData;

    public static BufferedImage refImg;
    public static BufferedImage refImgOriginal;
    public static File inFile;
    
    private static CaptionedImageView original;
    private static CaptionedImageView convoluted;
    private static CaptionedImageView pixelated;
    private static CaptionedImageView medianFiltered;
    private static Image originalImage;

    @Override
    public void start(Stage stage) throws IOException {
        String fileName = "PB/zdjecie2.jpeg";
        
        inFile = new File(fileName);
        refImg = ImageIO.read(inFile);
        refImgOriginal = ImageIO.read(inFile);

        final double[] height = {250};
        final double[] width = {250 * refImg.getWidth() / (double)refImg.getHeight()};
    
        originalImage = new Image(inFile.toURI().toString());
    
        original = new CaptionedImageView();
        original.setImage(originalImage);
        original.setText("Oryginał");
        original.setPrefImageHeight(height[0]);
        original.setPrefImageWidth(width[0]);
        
        pixelated = new CaptionedImageView();
        pixelated.setImage(Pixelation.apply(originalImage, 5));
        pixelated.setText("Pikselizacja");
        pixelated.setPrefImageHeight(height[0]);
        pixelated.setPrefImageWidth(width[0]);
    
    
        medianFiltered = new CaptionedImageView();
        medianFiltered.setImage(MedianFiltration.apply(originalImage, 1));
        medianFiltered.setText("Mediana");
        medianFiltered.setPrefImageHeight(height[0]);
        medianFiltered.setPrefImageWidth(width[0]);
    
        convoluted = new CaptionedImageView();
        convoluted.setImage(ConvolutionalFiltration.apply(originalImage, MaskType.SOBEL));
        convoluted.setText("Filtr konwolucyjny");
        convoluted.setPrefImageHeight(height[0]);
        convoluted.setPrefImageWidth(width[0]);
        
        updateChart(refImg);

        Button load = new Button("Załaduj");
        Slider slider = new Slider();
        Slider slider2 = new Slider();

        load.setOnAction(new EventHandler() {
            @Override
            @SneakyThrows
            public void handle(Event event) {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                inFile = fileChooser.showOpenDialog(stage);

                if (inFile != null && inFile.canRead()) {
                    refImg = ImageIO.read(inFile);
                    originalImage = new Image(inFile.toURI().toString());

                    final var copiedProps = ImageUtils.getPropsFromImage(refImg);
                    refImgOriginal = ImageUtils.copyImage(refImg, copiedProps);
                }

                slider.setValue(5);
                slider2.setValue(1);
                height[0] = 250;
                width[0] = 250 * refImg.getWidth()/(double) refImg.getHeight();

                original.setImage(originalImage);
                pixelated.setImage(Pixelation.apply(originalImage, 5));
                medianFiltered.setImage(MedianFiltration.apply(originalImage, 1));
                convoluted.setImage(ConvolutionalFiltration.apply(originalImage, MaskType.SOBEL));

                updateChart(refImg);
                convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

            }
        });

        slider.setMin(1);
        slider.setMax(40);
        slider.setValue(5);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
        slider.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        pixelated.setImage(Pixelation.apply(originalImage, (double) newValue)));

        slider2.setMin(1);
        slider2.setMax(15);
        slider2.setValue(1);
        slider2.setShowTickLabels(true);
        slider2.setShowTickMarks(true);
        slider2.setBlockIncrement(10);
        slider2.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        medianFiltered.setImage(MedianFiltration.apply(originalImage,(double) newValue)));

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(original, load);
        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(pixelated, slider);
        VBox vBox3 = new VBox();
        vBox3.getChildren().addAll(medianFiltered, slider2);
        
        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1, vBox2, vBox3, convoluted);
        
        Group root = new Group(hBox);
        Scene scene = new Scene(root, 1000, 600, Color.LIGHTGRAY);
        stage.setTitle("Binaryzacja");
        stage.setScene(scene);
        stage.show();
    }
	
	public static void main(String[] args) {
        launch();
    }

    private static void convertToBinarization(BufferedImage image, double value) {

        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[width][height];

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                result[row][col] = image.getRGB(row, col);

                int iRet = result[row][col];
                int iB = (iRet & 0xff);
                int iG = (( iRet & 0x00ff00) >> 8);
                int iR = (( iRet & 0xff0000) >> 16);
                int iAve = ( iR + iG + iB ) / 3;

            }
        }
    }


    private static void updateChart(BufferedImage image) {

        var histogramGreen = new int[256];
        var histogramRed = new int[256];
        var histogramBlue = new int[256];
        var histogramAverage = new int[256];
        histogramAverageData = histogramAverage;

        int width = image.getWidth();
        int height = image.getHeight();

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                int iRet = image.getRGB(row, col);

                int iB = (iRet & 0xff);
                int iG = (( iRet & 0x00ff00) >> 8);
                int iR = (( iRet & 0xff0000) >> 16);
                int iAve = ( iR + iG + iB ) / 3;

                ++histogramGreen[iG];
                ++histogramRed[iR];
                ++histogramBlue[iB];
                ++histogramAverage[iAve];
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void processChannel(double value, int row, int col, int iR, BufferedImage image, int zero, int one) {
        if (iR > value) {
            image.setRGB(row, col, one);
        } else {
            image.setRGB(row, col, zero);
        }
    }

    private static BufferedImage exposure(BufferedImage image, double exposure) {
        BufferedImage copy = refImgOriginal;
        
        if (exposure < 0) {
            exposure = (100 + exposure) / 100d;
            
            int width = copy.getWidth();
            int height = copy.getHeight();
    
            for (int row = 0; row < width; row++) {
                for (int col = 0; col < height; col++) {
                    int iRet = copy.getRGB(row, col);
            
            
                    int iR = (( iRet & 0xff0000) >> 16);
                    int iG = (( iRet & 0x00ff00) >> 8);
                    int iB = (iRet & 0xff);
            
                    iR = (int) (exposure * iR);
                    iG = (int) (exposure * iG);
                    iB = (int) (exposure * iB);

                    image.setRGB(row, col, (iR << 16) | (iG << 8) | iB);
                }
            }
            
        } else if (exposure > 0) {
            exposure = (100 - exposure) / 100d;
    
            int width = copy.getWidth();
            int height = copy.getHeight();
    
            for (int row = 0; row < width; row++) {
                for (int col = 0; col < height; col++) {
                    int iRet = copy.getRGB(row, col);
            
            
                    int iR = - ((( iRet & 0xff0000) >> 16) - 255);
                    int iG = - ((( iRet & 0x00ff00) >> 8) - 255);
                    int iB = - ((iRet & 0xff) - 255);
            
                    iR = - (((int) (exposure * iR)) - 255);
                    iG = - (((int) (exposure * iG)) - 255);
                    iB = - (((int) (exposure * iB)) - 255);

                    image.setRGB(row, col, (iR << 16) | (iG << 8) | iB);
                }
            }
        }
        
        return image;
    }

}