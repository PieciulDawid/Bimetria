package com.example.zadanie1;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Hashtable;

import javafx.embed.swing.SwingFXUtils;
import lombok.SneakyThrows;

public class HelloApplication extends Application {

    private static int[] histogramAverageData;

    public static BufferedImage imageOtsu;
    public static BufferedImage imageStretch;
    public static BufferedImage imageNiBlack;
    public static BufferedImage refImg;
    public static BufferedImage refImgOriginal;
    public static File inFile;

    @Override
    public void start(Stage stage) throws IOException {
        String fileName = "zdjecie.jpeg";
        inFile = new File(fileName);
        refImg = ImageIO.read(inFile);
        refImgOriginal = ImageIO.read(inFile);
        imageStretch = ImageIO.read(inFile);
        imageNiBlack = ImageIO.read(inFile);
        imageOtsu = ImageIO.read(inFile);

        final double[] height = {250};
        final double[] width = {250 * refImg.getWidth() / (double)refImg.getHeight()};

        ImageView imageViewOrginal = new ImageView();
        imageViewOrginal.setFitHeight(height[0]);
        imageViewOrginal.setFitWidth(width[0]);
        imageViewOrginal.setImage(convertToFxImage(refImg));

        ImageView imageViewNiBlack = new ImageView();
        imageViewNiBlack.setFitHeight(height[0]);
        imageViewNiBlack.setFitWidth(width[0]);
        imageNiBlack = NiBlack.binarize(refImg, 3);
        final Image[] imageN = {convertToFxImage(imageNiBlack)};
        imageViewNiBlack.setImage(imageN[0]);

        ImageView imageViewStretch = new ImageView();
        imageViewStretch.setFitHeight(height[0]);
        imageViewStretch.setFitWidth(width[0]);
        imageStretch = HistogramStretchUtil.stretchHistogram(refImg);
        final Image[] imageS = {convertToFxImage(imageStretch)};
        imageViewStretch.setImage(imageS[0]);

        updateChart(refImg, imageStretch, imageNiBlack);

        convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

        ImageView imageViewOtsu = new ImageView();
        imageViewOtsu.setFitHeight(height[0]);
        imageViewOtsu.setFitWidth(width[0]);
        final Image[] imageO = {convertToFxImage(imageOtsu)};
        imageViewOtsu.setImage(imageO[0]);

        Button load = new Button("Załaduj");

        load.setOnAction(new EventHandler() {
            @Override
            @SneakyThrows
            public void handle(Event event) {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                inFile = fileChooser.showOpenDialog(stage);

                if (inFile != null && inFile.canRead()) {
                    refImg = ImageIO.read(inFile);

                    final var copiedProps = getPropsFromImage(refImg);
                    refImgOriginal = copyImage(refImg, copiedProps);
                    imageStretch = copyImage(refImg, copiedProps);
                    imageNiBlack = copyImage(refImg, copiedProps);
                    imageOtsu = copyImage(refImg, copiedProps);
                }

                height[0] = 250;
                width[0] = 250 * refImg.getWidth()/(double) refImg.getHeight();

                imageViewOrginal.setFitHeight(height[0]);
                imageViewOrginal.setFitWidth(width[0]);
                imageViewOrginal.setImage(convertToFxImage(refImg));

                imageStretch = HistogramStretchUtil.stretchHistogram(refImg);
                imageViewStretch.setFitHeight(height[0]);
                imageViewStretch.setFitWidth(width[0]);
                imageViewStretch.setImage(convertToFxImage(imageStretch));

                imageNiBlack = NiBlack.binarize(refImg, 3);
                imageViewNiBlack.setFitHeight(height[0]);
                imageViewNiBlack.setFitWidth(width[0]);
                imageViewNiBlack.setImage(convertToFxImage(imageNiBlack));

                updateChart(refImg, imageStretch, imageNiBlack);
                convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

                imageViewOtsu.setFitHeight(height[0]);
                imageViewOtsu.setFitWidth(width[0]);
                imageViewOtsu.setImage(convertToFxImage(imageOtsu));
            }
        });


        Slider slider = new Slider();
        slider.setMin(1);
        slider.setMax(20);
        slider.setValue(3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
        slider.valueProperty().addListener(
                new ChangeListener<Number>() {

                    public void changed(
                            ObservableValue<? extends Number> observable,
                            Number oldValue,
                            Number newValue) {

                        imageViewNiBlack.setImage(convertToFxImage(NiBlack.binarize(refImg, (double) newValue)));
                    }
                });

        Text labelOrginal = new Text("Orginał");
        Text labelOtsu = new Text("Otsu");
        Text labelNiBlack = new Text("NiBlack");
        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(labelOrginal, imageViewOrginal, load);
        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(labelOtsu, imageViewOtsu);
        VBox vBox3 = new VBox();
        vBox3.getChildren().addAll(labelNiBlack, imageViewNiBlack, slider);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1, vBox2, vBox3);
        Group root = new Group(hBox);
        Scene scene = new Scene(root, 1200, 480);
        stage.setTitle("Binaryzacja");
        stage.setScene(scene);
        stage.show();
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

    private static Image convertToFxImage(BufferedImage image) {
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
        return new ImageView(wr).getImage();
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

                processChannel(value, row, col, iAve, imageOtsu, 0, 0xffffff);
            }
        }
    }


    private static void updateChart(BufferedImage image, BufferedImage imageStretch, BufferedImage imageAlign) {

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

        histogramGreen = new int[256];
        histogramRed = new int[256];
        histogramBlue = new int[256];
        histogramAverage = new int[256];

        width = imageStretch.getWidth();
        height = imageStretch.getHeight();

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                int iRet = imageStretch.getRGB(row, col);

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

        histogramGreen = new int[256];
        histogramRed = new int[256];
        histogramBlue = new int[256];
        histogramAverage = new int[256];

        width = imageAlign.getWidth();
        height = imageAlign.getHeight();

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                int iRet = imageAlign.getRGB(row, col);

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