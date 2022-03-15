package com.example.zadanie1;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import lombok.SneakyThrows;

public class HelloApplication extends Application {
    
    public static BufferedImage imageBlue;
    public static BufferedImage imageRed;
    public static BufferedImage imageGreen;
    public static BufferedImage imageAverage;
    public static BufferedImage refImg;
    
    public static File inFile;
    
    private static LineChart<String, Number> chartHistogram;
    
    static XYChart.Series<String, Number> seriesAverage;
    static XYChart.Series<String, Number> seriesRed;
    static XYChart.Series<String, Number> seriesGreen;
    static XYChart.Series<String, Number> seriesBlue;

    @Override
    public void start(Stage stage) throws IOException {
        String fileName = "zdjecie.jpeg";
        inFile = new File(fileName);
        refImg = ImageIO.read(inFile);
        imageBlue = ImageIO.read(inFile);
        imageRed = ImageIO.read(inFile);
        imageGreen = ImageIO.read(inFile);
        imageAverage = ImageIO.read(inFile);

        final double[] height = {200};
        final double[] width = {200 * refImg.getWidth() / (double)refImg.getHeight()};


        ImageView imageView = new ImageView();
        imageView.setFitHeight(height[0]);
        imageView.setFitWidth(width[0]);
        imageView.setImage(convertToFxImage(refImg));
        convertToBinarization(refImg, 120);

        ImageView imageViewA = new ImageView();
        imageViewA.setFitHeight(height[0]);
        imageViewA.setFitWidth(width[0]);
        final Image[] imageA = {convertToFxImage(imageAverage)};
        imageViewA.setImage(imageA[0]);

        ImageView imageViewG = new ImageView();
        imageViewG.setFitHeight(height[0]);
        imageViewG.setFitWidth(width[0]);
        final Image[] imageG = {convertToFxImage(imageGreen)};
        imageViewG.setImage(imageG[0]);

        ImageView imageViewB = new ImageView();
        imageViewB.setFitHeight(height[0]);
        imageViewB.setFitWidth(width[0]);
        final Image[] imageB = {convertToFxImage(imageBlue)};
        imageViewB.setImage(imageB[0]);

        ImageView imageViewR = new ImageView();
        imageViewR.setFitHeight(height[0]);
        imageViewR.setFitWidth(width[0]);
        final Image[] imageR = {convertToFxImage(imageRed)};
        imageViewR.setImage(imageR[0]);

        initializeChart();
        updateChart(refImg);

        Button load = new Button("Załaduj");
        Button save = new Button("Zapisz");

        load.setOnAction(new EventHandler() {
            @Override
            @SneakyThrows
            public void handle(Event event) {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                inFile = fileChooser.showOpenDialog(stage);
                
                if (inFile != null && inFile.canRead()) {
                    refImg = ImageIO.read(inFile);
                    updateChart(refImg);
    
                    final var copiedProps = getPropsFromImage(refImg);
    
                    imageBlue = copyImage(refImg, copiedProps);
                    imageRed = copyImage(refImg, copiedProps);
                    imageGreen = copyImage(refImg, copiedProps);
                    imageAverage = copyImage(refImg, copiedProps);
                }

                height[0] = 200;
                width[0] = 200 * refImg.getWidth()/(double) refImg.getHeight();

                imageView.setFitHeight(height[0]);
                imageView.setFitWidth(width[0]);
                imageView.setImage(convertToFxImage(refImg));
                convertToBinarization(refImg, 120);

                imageViewA.setFitHeight(height[0]);
                imageViewA.setFitWidth(width[0]);
                imageViewA.setImage(convertToFxImage(imageAverage));

                imageViewG.setFitHeight(height[0]);
                imageViewG.setFitWidth(width[0]);
                imageViewG.setImage(convertToFxImage(imageGreen));

                imageViewB.setFitHeight(height[0]);
                imageViewB.setFitWidth(width[0]);
                imageViewB.setImage(convertToFxImage(imageBlue));

                imageViewR.setFitHeight(height[0]);
                imageViewR.setFitWidth(width[0]);
                imageViewR.setImage(convertToFxImage(imageRed));
            }
        });

        save.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                File outputFile = new File("wynik"+".png");
                BufferedImage bImage = SwingFXUtils.fromFXImage(convertToFxImage(imageAverage), null);
                try {
                    ImageIO.write(bImage, "png", outputFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(255);
        slider.setValue(120);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(10);
        slider.valueProperty().addListener(
                new ChangeListener<Number>() {

                    public void changed(
                            ObservableValue<? extends Number> observable,
                            Number oldValue,
                            Number newValue) {
                        
                        convertToBinarization(refImg, (double) newValue);
                        imageA[0] = convertToFxImage(imageAverage);
                        imageViewA.setImage(imageA[0]);

                        imageG[0] = convertToFxImage(imageGreen);
                        imageViewG.setImage(imageG[0]);

                        imageB[0] = convertToFxImage(imageBlue);
                        imageViewB.setImage(imageB[0]);

                        imageR[0] = convertToFxImage(imageRed);
                        imageViewR.setImage(imageR[0]);
                    }
                });

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(imageView, imageViewA, slider, load, save);
        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(imageViewG, imageViewB, imageViewR);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1, vBox2, chartHistogram);
        Group root = new Group(hBox);
        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Binaryzacja i Histogram");
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
                
                processChannel(value, row, col, iAve, imageAverage, 0, 0xffffff);
                processChannel(value, row, col, iB, imageBlue, 0xff, 0xffffff);
                processChannel(value, row, col, iG, imageGreen, 0x00ff00, 0xffffff);
                processChannel(value, row, col, iR, imageRed, 0xff0000, 0xffffff);
            }
        }
    }
    
    private static void initializeChart() {
        var xAxis = new CategoryAxis();
        var yAxis = new NumberAxis();
        
        chartHistogram = new LineChart<>(xAxis, yAxis);
        
        chartHistogram.setCreateSymbols(false);
        chartHistogram.setHorizontalGridLinesVisible(false);
        chartHistogram.setVerticalGridLinesVisible(false);
        
        seriesAverage = new XYChart.Series<>();
        seriesRed = new XYChart.Series<>();
        seriesGreen = new XYChart.Series<>();
        seriesBlue = new XYChart.Series<>();
        
        seriesAverage.setName("average");
        seriesRed.setName("red");
        seriesGreen.setName("green");
        seriesBlue.setName("blue");
        
        chartHistogram.getData().addAll(
                List.of(seriesRed, seriesAverage, seriesGreen, seriesBlue));
    }
    
    private static void updateChart(BufferedImage image) {
    
        var histogramGreen = new int[256];
        var histogramRed = new int[256];
        var histogramBlue = new int[256];
        var histogramAverage = new int[256];
    
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
    
        seriesRed.getData().clear();
        seriesGreen.getData().clear();
        seriesBlue.getData().clear();
        seriesAverage.getData().clear();
        
        for (int i = 0; i < 256; i++) {
            seriesRed.getData().add(new XYChart.Data<>(String.valueOf(i), histogramRed[i]));
            seriesGreen.getData().add(new XYChart.Data<>(String.valueOf(i), histogramGreen[i]));
            seriesBlue.getData().add(new XYChart.Data<>(String.valueOf(i), histogramBlue[i]));
            seriesAverage.getData().add(new XYChart.Data<>(String.valueOf(i), histogramAverage[i]));
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
    
}