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

    private static int[] histogramAverageData;

    public static BufferedImage imageAverage;
    public static BufferedImage imageStretch;
    public static BufferedImage imageAlign;
    public static BufferedImage refImg;

    public static File inFile;

    private static LineChart<String, Number> chartHistogram;
    private static LineChart<String, Number> chartHistogramStretch;
    private static LineChart<String, Number> chartHistogramAlign;

    //histogram do orginalnego zdjecia
    static XYChart.Series<String, Number> seriesAverage;
    static XYChart.Series<String, Number> seriesRed;
    static XYChart.Series<String, Number> seriesGreen;
    static XYChart.Series<String, Number> seriesBlue;

    //histogram do zdjęcia z rozciągniętym histogramem
    static XYChart.Series<String, Number> seriesAverageStretch;
    static XYChart.Series<String, Number> seriesRedStretch;
    static XYChart.Series<String, Number> seriesGreenStretch;
    static XYChart.Series<String, Number> seriesBlueStretch;

    //histogram do zdjecia z rozciagniętym histogramem
    static XYChart.Series<String, Number> seriesAverageAlign;
    static XYChart.Series<String, Number> seriesRedAlign;
    static XYChart.Series<String, Number> seriesGreenAlign;
    static XYChart.Series<String, Number> seriesBlueAlign;

    @Override
    public void start(Stage stage) throws IOException {
        String fileName = "zdjecie.jpeg";
        inFile = new File(fileName);
        refImg = ImageIO.read(inFile);
        imageStretch = ImageIO.read(inFile);
        imageAlign = ImageIO.read(inFile);
        imageAverage = ImageIO.read(inFile);

        final double[] height = {250};
        final double[] width = {250 * refImg.getWidth() / (double)refImg.getHeight()};


        ImageView imageView = new ImageView();
        imageView.setFitHeight(height[0]);
        imageView.setFitWidth(width[0]);
        imageView.setImage(convertToFxImage(refImg));

        //TODO tutaj dodać metodę wyliczajacą próg - metoda Otsu
        //ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth());
        //convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));



        ImageView imageViewStretch = new ImageView();
        imageViewStretch.setFitHeight(height[0]);
        imageViewStretch.setFitWidth(width[0]);
        imageStretch = HistogramStretchUtil.stretchHistogram(refImg);
        final Image[] imageS = {convertToFxImage(imageStretch)};
        imageViewStretch.setImage(imageS[0]);

        ImageView imageViewAlign = new ImageView();
        imageViewAlign.setFitHeight(height[0]);
        imageViewAlign.setFitWidth(width[0]);
        //TODO tutaj wstawić wyrówanie histogramu
        imageAlign = HistogramAlignUtil.alignHistogram(refImg);
        final Image[] imageAl = {convertToFxImage(imageAlign)};
        imageViewAlign.setImage(imageAl[0]);


        initializeChart();
        updateChart(refImg, imageStretch, imageAlign);

        convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

        ImageView imageViewA = new ImageView();
        imageViewA.setFitHeight(height[0]);
        imageViewA.setFitWidth(width[0]);
        final Image[] imageA = {convertToFxImage(imageAverage)};
        imageViewA.setImage(imageA[0]);

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


                    final var copiedProps = getPropsFromImage(refImg);

                    imageStretch = copyImage(refImg, copiedProps);
                    imageAlign = copyImage(refImg, copiedProps);
                    imageAverage = copyImage(refImg, copiedProps);
                }

                height[0] = 250;
                width[0] = 250 * refImg.getWidth()/(double) refImg.getHeight();

                imageView.setFitHeight(height[0]);
                imageView.setFitWidth(width[0]);
                imageView.setImage(convertToFxImage(refImg));

                imageStretch = HistogramStretchUtil.stretchHistogram(refImg);
                imageViewStretch.setFitHeight(height[0]);
                imageViewStretch.setFitWidth(width[0]);
                imageViewStretch.setImage(convertToFxImage(imageStretch));


                imageAlign = HistogramAlignUtil.alignHistogram(refImg);
                imageViewAlign.setFitHeight(height[0]);
                imageViewAlign.setFitWidth(width[0]);
                imageViewAlign.setImage(convertToFxImage(imageAlign));

                updateChart(refImg, imageStretch, imageAlign);
                convertToBinarization(refImg, ThresholdingUtil.findThresholdForOtsus(histogramAverageData, refImg.getHeight()*refImg.getWidth()));

                imageViewA.setFitHeight(height[0]);
                imageViewA.setFitWidth(width[0]);
                imageViewA.setImage(convertToFxImage(imageAverage));
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

//                        convertToBinarization(refImg, (double) newValue);
//                        imageA[0] = convertToFxImage(imageAverage);
//                        imageViewA.setImage(imageA[0]);

                        exposure((double) newValue);
                        imageView.setImage(convertToFxImage(refImg));
                    }
                });

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(imageViewA, slider, load, save);
        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(imageView, imageViewStretch, imageViewAlign);
        VBox vBox3 = new VBox();
        vBox3.getChildren().addAll(chartHistogram, chartHistogramStretch, chartHistogramAlign);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox1, vBox2, vBox3);
        Group root = new Group(hBox);
        Scene scene = new Scene(root, 1000, 780);
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
            }
        }
    }

    private static void initializeChart() {
        var xAxis = new CategoryAxis();
        var yAxis = new NumberAxis();
        var xAxis2 = new CategoryAxis();
        var yAxis2 = new NumberAxis();
        var xAxis3 = new CategoryAxis();
        var yAxis3 = new NumberAxis();

        chartHistogram = new LineChart<>(xAxis, yAxis);
        chartHistogramStretch = new LineChart<>(xAxis2, yAxis2);
        chartHistogramAlign = new LineChart<>(xAxis3, yAxis3);

        chartHistogram.setMaxWidth(450);
        chartHistogram.setMaxHeight(250);

        chartHistogramStretch.setMaxWidth(450);
        chartHistogramStretch.setMaxHeight(250);

        chartHistogramAlign.setMaxWidth(450);
        chartHistogramAlign.setMaxHeight(250);

        chartHistogram.setCreateSymbols(false);
        chartHistogram.setHorizontalGridLinesVisible(false);
        chartHistogram.setVerticalGridLinesVisible(false);

        chartHistogramStretch.setCreateSymbols(false);
        chartHistogramStretch.setHorizontalGridLinesVisible(false);
        chartHistogramStretch.setVerticalGridLinesVisible(false);

        chartHistogramAlign.setCreateSymbols(false);
        chartHistogramAlign.setHorizontalGridLinesVisible(false);
        chartHistogramAlign.setVerticalGridLinesVisible(false);

        seriesAverage   = new XYChart.Series<>();
        seriesRed       = new XYChart.Series<>();
        seriesGreen     = new XYChart.Series<>();
        seriesBlue      = new XYChart.Series<>();

        seriesAverageStretch   = new XYChart.Series<>();
        seriesRedStretch       = new XYChart.Series<>();
        seriesGreenStretch     = new XYChart.Series<>();
        seriesBlueStretch      = new XYChart.Series<>();

        seriesAverageAlign   = new XYChart.Series<>();
        seriesRedAlign       = new XYChart.Series<>();
        seriesGreenAlign     = new XYChart.Series<>();
        seriesBlueAlign      = new XYChart.Series<>();

        seriesAverage.setName("average");
        seriesRed.setName("red");
        seriesGreen.setName("green");
        seriesBlue.setName("blue");

        seriesAverageStretch.setName("average");
        seriesRedStretch.setName("red");
        seriesGreenStretch.setName("green");
        seriesBlueStretch.setName("blue");

        seriesAverageAlign.setName("average");
        seriesRedAlign.setName("red");
        seriesGreenAlign.setName("green");
        seriesBlueAlign.setName("blue");

        chartHistogram.getData().addAll(
                List.of(seriesRed, seriesAverage, seriesGreen, seriesBlue));

        chartHistogramStretch.getData().addAll(
                List.of(seriesRedStretch, seriesAverageStretch, seriesGreenStretch, seriesBlueStretch));

        chartHistogramAlign.getData().addAll(
                List.of(seriesRedAlign, seriesAverageAlign, seriesGreenAlign, seriesBlueAlign));
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

        seriesRedStretch.getData().clear();
        seriesGreenStretch.getData().clear();
        seriesBlueStretch.getData().clear();
        seriesAverageStretch.getData().clear();

        for (int i = 0; i < 256; i++) {
            seriesRedStretch.getData().add(new XYChart.Data<>(String.valueOf(i), histogramRed[i]));
            seriesGreenStretch.getData().add(new XYChart.Data<>(String.valueOf(i), histogramGreen[i]));
            seriesBlueStretch.getData().add(new XYChart.Data<>(String.valueOf(i), histogramBlue[i]));
            seriesAverageStretch.getData().add(new XYChart.Data<>(String.valueOf(i), histogramAverage[i]));
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

        seriesRedAlign.getData().clear();
        seriesGreenAlign.getData().clear();
        seriesBlueAlign.getData().clear();
        seriesAverageAlign.getData().clear();

        for (int i = 0; i < 256; i++) {
            seriesRedAlign.getData().add(new XYChart.Data<>(String.valueOf(i), histogramRed[i]));
            seriesGreenAlign.getData().add(new XYChart.Data<>(String.valueOf(i), histogramGreen[i]));
            seriesBlueAlign.getData().add(new XYChart.Data<>(String.valueOf(i), histogramBlue[i]));
            seriesAverageAlign.getData().add(new XYChart.Data<>(String.valueOf(i), histogramAverage[i]));
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

    private static void exposure(double exposure){

        int licznik = (int)(140 -  exposure)/10;
        for (int row = 0; row < refImg.getWidth(); row++) {
            for (int col = 0; col < refImg.getHeight(); col++) {
                int iRet = refImg.getRGB(row, col);

                int iR = (( iRet & 0xff0000) >> 16)+licznik;
                int iG = (( iRet & 0x00ff00) >> 8)+licznik;
                int iB = (iRet & 0xff)+licznik;

                refImg.setRGB(row, col, (iR << 16) | (iG << 8) | iB);
            }
        }
    }

}