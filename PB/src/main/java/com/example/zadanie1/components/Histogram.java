package com.example.zadanie1.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;


public class Histogram extends LineChart<String, Number> {
	private final SimpleObjectProperty<int[]> average;
	private final SimpleObjectProperty<int[]> red;
	private final SimpleObjectProperty<int[]> green;
	private final SimpleObjectProperty<int[]> blue;
	
	public Histogram() {
		super(new CategoryAxis(), new NumberAxis());
		
		this.setCreateSymbols(false);
		this.setHorizontalGridLinesVisible(false);
		this.setVerticalGridLinesVisible(false);
		
		var averageSeries = new XYChart.Series<String, Number>();
		var redSeries = new XYChart.Series<String, Number>();
		var greenSeries = new XYChart.Series<String, Number>();
		var blueSeries = new XYChart.Series<String, Number>();
		
		averageSeries.setName("average");
		redSeries.setName("red");
		greenSeries.setName("green");
		blueSeries.setName("blue");
		
		average = new SimpleObjectProperty<>(new int[0]);
		red = new SimpleObjectProperty<>(new int[0]);
		green = new SimpleObjectProperty<>(new int[0]);
		blue = new SimpleObjectProperty<>(new int[0]);
		
		average.addListener(updateOnNewData(averageSeries));
		red.addListener(updateOnNewData(redSeries));
		green.addListener(updateOnNewData(greenSeries));
		blue.addListener(updateOnNewData(blueSeries));
	}
	
	public int[] getAverage() {
		return average.get();
	}
	
	public SimpleObjectProperty<int[]> averageProperty() {
		return average;
	}
	
	public void setAverage(int[] average) {
		this.average.set(average);
	}
	
	public int[] getRed() {
		return red.get();
	}
	
	public SimpleObjectProperty<int[]> redProperty() {
		return red;
	}
	
	public void setRed(int[] red) {
		this.red.set(red);
	}
	
	public int[] getGreen() {
		return green.get();
	}
	
	public SimpleObjectProperty<int[]> greenProperty() {
		return green;
	}
	
	public void setGreen(int[] green) {
		this.green.set(green);
	}
	
	public int[] getBlue() {
		return blue.get();
	}
	
	public SimpleObjectProperty<int[]> blueProperty() {
		return blue;
	}
	
	public void setBlue(int[] blue) {
		this.blue.set(blue);
	}
	
	
	private TypedInvalidationListener<SimpleObjectProperty<int[]>> updateOnNewData(
			XYChart.Series<String, Number> series) {
		
		return (observable) -> {
			var seriesData = series.getData();
			var arrayData = observable.get();
			
			seriesData.clear();
			
			for (int i = 0; i < arrayData.length; i++) {
				seriesData.add(new XYChart.Data<>(String.valueOf(i), arrayData[i]));
			}
		};
	}
	
	private interface TypedInvalidationListener<T> extends InvalidationListener {
		
		@Override
		@SuppressWarnings("unchecked")
		default void invalidated(Observable observable) {
			invalidatedImpl((T) observable);
		}
		
		void invalidatedImpl(T observable);
		
	}
}

