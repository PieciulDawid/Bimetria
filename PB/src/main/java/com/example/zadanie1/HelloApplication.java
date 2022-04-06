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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import lombok.SneakyThrows;

public class HelloApplication extends Application {

    private static int[] histogramAverageData;

    public static BufferedImage refImg;
    public static BufferedImage refImgOriginal;
    public static File inFile;

    private static CaptionedImageView bucketTool;
    private static Image originalImage;

    @Override
    public void start(Stage stage) throws IOException {
        String fileName = "zdjecie2.jpeg";
        
        inFile = new File(fileName);
        refImg = ImageIO.read(inFile);
        refImgOriginal = ImageIO.read(inFile);

        final double[] height = {550};
        final double[] width = {550 * refImg.getWidth() / (double)refImg.getHeight()};
    
        originalImage = new Image(inFile.toURI().toString());

        bucketTool = new CaptionedImageView();
        //bucketTool.getImageView().setOnMouseClicked(EventUtil.getPixelSelectionOnImageHandler(null));
        bucketTool.setImage(originalImage);
        bucketTool.setText("Magiczna różdzka");
        bucketTool.setPrefImageHeight(height[0]);
        bucketTool.setPrefImageWidth(width[0]);
        
        updateChart(refImg);

        Button load = new Button("Załaduj");
        Button restart = new Button("Restart");
        Button global = new Button("Globalnie");
        Slider slider = new Slider();

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
                height[0] = 250;
                width[0] = 250 * refImg.getWidth()/(double) refImg.getHeight();

                bucketTool.setImage(ConvolutionalFiltration.apply(originalImage, MaskType.SOBEL));

                updateChart(refImg);
                convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

            }
        });

        slider.setMin(0);
        slider.setMax(255);
        slider.setValue(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
/*
        slider.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        pixelated.setImage(Pixelation.apply(originalImage, (double) newValue)));
*/


        HBox hBox0 = new HBox();
        hBox0.getChildren().addAll(load, restart, global, slider);
        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(bucketTool, hBox0);
        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll();


        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1, vBox2);
        
        Group root = new Group(hBox);
        Scene scene = new Scene(root, 500, 700, Color.LIGHTGRAY);
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