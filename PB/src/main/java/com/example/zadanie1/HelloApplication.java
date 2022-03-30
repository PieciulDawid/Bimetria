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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Hashtable;

import lombok.SneakyThrows;

public class HelloApplication extends Application {

    private static int[] histogramAverageData;

    public static BufferedImage refImg;
    public static BufferedImage refImgOriginal;
    public static BufferedImage imagePixelation;
    public static BufferedImage imageMedian;
    public static File inFile;

    @Override
    public void start(Stage stage) throws IOException {
        String fileName = "zdjecie3.jpeg";
        inFile = new File(fileName);
        refImg = ImageIO.read(inFile);
        refImgOriginal = ImageIO.read(inFile);
        imagePixelation = ImageIO.read(inFile);
        imageMedian = ImageIO.read(inFile);

        final double[] height = {250};
        final double[] width = {250 * refImg.getWidth() / (double)refImg.getHeight()};

        ImageView imageViewOrginal = new ImageView();
        imageViewOrginal.setFitHeight(height[0]);
        imageViewOrginal.setFitWidth(width[0]);
        imageViewOrginal.setImage(convertToFxImage(refImg));

        ImageView imageViewPixelation = new ImageView();
        imageViewPixelation.setFitHeight(height[0]);
        imageViewPixelation.setFitWidth(width[0]);
        imagePixelation = Pixelation.doPixelation(refImg, 5);
        final Image[] imageP = {convertToFxImage(imagePixelation)};
        imageViewPixelation.setImage(imageP[0]);

        ImageView imageViewMedian = new ImageView();
        imageViewMedian.setFitHeight(height[0]);
        imageViewMedian.setFitWidth(width[0]);
        imagePixelation = Median.doMedian(refImg, 1);
        final Image[] imageM = {convertToFxImage(imagePixelation)};
        imageViewMedian.setImage(imageM[0]);

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

                    final var copiedProps = getPropsFromImage(refImg);
                    refImgOriginal = copyImage(refImg, copiedProps);
                }

                slider.setValue(3);
                slider2.setValue(1);
                height[0] = 250;
                width[0] = 250 * refImg.getWidth()/(double) refImg.getHeight();

                imageViewOrginal.setFitHeight(height[0]);
                imageViewOrginal.setFitWidth(width[0]);
                imageViewOrginal.setImage(convertToFxImage(refImg));

                imagePixelation = Pixelation.doPixelation(refImg, 5);
                imageViewPixelation.setFitHeight(height[0]);
                imageViewPixelation.setFitWidth(width[0]);
                imageViewPixelation.setImage(convertToFxImage(imagePixelation));

                imageMedian = Median.doMedian(refImg, 1);
                imageViewMedian.setFitHeight(height[0]);
                imageViewMedian.setFitWidth(width[0]);
                imageViewMedian.setImage(convertToFxImage(imageMedian));


                updateChart(refImg);
                convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

            }
        });

        slider.setMin(1);
        slider.setMax(40);
        slider.setValue(1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
        slider.valueProperty().addListener(
                new ChangeListener<Number>() {

                    public void changed(
                            ObservableValue<? extends Number> observable,
                            Number oldValue,
                            Number newValue) {

                        imageViewPixelation.setImage(convertToFxImage(Pixelation.doPixelation(refImg,(double) newValue)));

                    }
                });

        slider2.setMin(1);
        slider2.setMax(15);
        slider2.setValue(1);
        slider2.setShowTickLabels(true);
        slider2.setShowTickMarks(true);
        slider2.setBlockIncrement(10);
        slider2.valueProperty().addListener(
                new ChangeListener<Number>() {

                    public void changed(
                            ObservableValue<? extends Number> observable,
                            Number oldValue,
                            Number newValue) {

                        imageViewMedian.setImage(convertToFxImage(Median.doMedian(refImg,(double) newValue)));

                    }
                });

        Text labelOrginal = new Text("Orginał");
        Text labelPixelation = new Text("Pikselizacja");
        Text labelMedian = new Text("Mediana");


        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(labelOrginal, imageViewOrginal, load);
        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(labelPixelation, imageViewPixelation, slider);
        HBox hBox = new HBox();
        VBox vBox3 = new VBox();
        vBox3.getChildren().addAll(labelMedian, imageViewMedian, slider2);
        hBox.getChildren().addAll(vBox1, vBox2, vBox3);
        Group root = new Group(hBox);
        Scene scene = new Scene(root, 1000, 600, Color.LIGHTGRAY);
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