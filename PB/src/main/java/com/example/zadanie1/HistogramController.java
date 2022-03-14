package com.example.zadanie1;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

public class HistogramController {
	
	@FXML
	private LineChart<String, Number> histogram;
	
	@FXML
	private XYChart.Series<String, Number> seriesAverage;
	
	@FXML
	private XYChart.Series<String, Number> seriesRed;
	
	@FXML
	private XYChart.Series<String, Number> seriesGreen;
	
	@FXML
	private XYChart.Series<String, Number> seriesBlue;
	
	
	
	@FXML
	private void initialize() {
		var xAxis = histogram.getXAxis();
		var yAxis = histogram.getYAxis();
		
		
		histogram.setCreateSymbols(false);
		histogram.setHorizontalGridLinesVisible(false);
		histogram.setVerticalGridLinesVisible(false);
		
		histogram.getData().addAll(
				List.of(seriesRed, seriesAverage, seriesGreen, seriesBlue));
		
	}
}
