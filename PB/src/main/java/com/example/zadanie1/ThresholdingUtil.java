package com.example.zadanie1;

import java.awt.image.BufferedImage;

public class ThresholdingUtil {
	
	public static int findThresholdForOtsus(int[] histogramData, int pixelCount) {
		double valueOfMax = 0;
		int indexOfMax = 0;
		
		int lessThenThresholdCount = 0;
		
		for (int currThreshold = 1; currThreshold < histogramData.length; currThreshold++) {
			lessThenThresholdCount += histogramData[currThreshold];
			
			double currVariance = 0;
			double omega0 = lessThenThresholdCount / (double) pixelCount;
			
			double stDev0 = standardDev(histogramData, 0, currThreshold, lessThenThresholdCount);
			double stDev1 = standardDev(histogramData, currThreshold, 256, lessThenThresholdCount);
			
			
			currVariance = omega0 * stDev0 + (1 - omega0) * stDev1;
			
			if (valueOfMax < currVariance) {
				valueOfMax = currVariance;
				indexOfMax = currThreshold;
			}
		}
		
		return indexOfMax;
	}
	
	private static double standardDev(int[] histogramData, int start, int end, int count) {
		double mean = mean(histogramData, start, end, count);
		
		double sum = 0;
		
		for (int i = start; i < end; i++) {
			sum += histogramData[i] * Math.pow(mean - i, 2);
		}
		
		return sum / count;
	}
	
	private static double mean(int[] histogramData, int start, int end, int count) {
		double sum = 0;
		
		for (int i = start; i < end; i++) {
			sum += histogramData[i] * i;
		}
		
		return sum / count;
	}
}
