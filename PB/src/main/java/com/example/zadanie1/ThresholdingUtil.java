package com.example.zadanie1;

public class ThresholdingUtil {
	
	public static int findThresholdForOtsus(int[] histogramData, int pixelCount) {
		MeasureCalculator measureCalculator = (
				_histogramData,
				_pixelCount,
				lessThenThresholdCount,
				currThreshold) -> {
			
			double omega0 = lessThenThresholdCount / (double) _pixelCount;
			
			double stDev0 = standardDev(
					_histogramData,
					0,
					currThreshold,
					lessThenThresholdCount);
			double stDev1 = standardDev(
					_histogramData,
					currThreshold,
					_histogramData.length,
					_pixelCount - lessThenThresholdCount);
			
			
			return omega0 * stDev0 + (1 - omega0) * stDev1;
		};
		
		return findThresholdWithHighestMeasure(histogramData, pixelCount, measureCalculator);
	}
	
	public static int findThresholdForKapurs(int[] histogramData, int pixelCount) {
		MeasureCalculator measureCalculator = (
				_histogramData,
				_pixelCount,
				lessThenThresholdCount,
				currThreshold) -> {
			
			double entropy0 = entropy(
					_histogramData,
					0,
					currThreshold,
					lessThenThresholdCount);
			
			double entropy1 = entropy(
					_histogramData,
					currThreshold,
					_histogramData.length,
					_pixelCount - lessThenThresholdCount);
			
			
			return entropy0 + entropy1;
		};
		
		return findThresholdWithHighestMeasure(histogramData, pixelCount, measureCalculator);
	}
	
	public static int findThresholdForLuWu(int[] histogramData, int pixelCount) {
		MeasureCalculator measureCalculator = (
				_histogramData,
				_pixelCount,
				lessThenThresholdCount,
				currThreshold) -> {
			
			double entropy0 = entropy(
					_histogramData,
					0,
					currThreshold,
					lessThenThresholdCount);
						
			double entropy1 = entropy(
					_histogramData,
					currThreshold,
					_histogramData.length,
					_pixelCount - lessThenThresholdCount);
			
			
			return -(entropy1 - entropy0);
		};
		
		return findThresholdWithHighestMeasure(histogramData, pixelCount, measureCalculator);
	}
	
	
	public static int findThresholdWithHighestMeasure(
			int[] histogramData,
			int pixelCount,
			MeasureCalculator measureCalculator) {
		
		double valueOfMax = 0;
		int indexOfMax = 0;
		
		int lessThenThresholdCount = 0;
		
		
		for (int currThreshold = 1; currThreshold < histogramData.length; currThreshold++) {
			lessThenThresholdCount += histogramData[currThreshold];
			
			double currMeasureValue = measureCalculator.calculate(histogramData, pixelCount, lessThenThresholdCount, currThreshold);
			
			if (valueOfMax < currMeasureValue) {
				valueOfMax = currMeasureValue;
				indexOfMax = currThreshold;
			}
		}
		
		return indexOfMax;
	}
	
	@FunctionalInterface
	public interface MeasureCalculator {
		/**
		 * Calculates measure for a specified histogram and threshold
		 *
		 * @param histogramData amount of pixels corresponding to specified brightness level
		 * @param allPixels
		 * @param belowThreshold
		 * @param currThreshold
		 * @return calculated measure value
		 */
		double calculate(int[] histogramData, int allPixels, int belowThreshold, int currThreshold);
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
	
	private static double entropy(int[] histogramData, int start, int end, int count) {
		double sum = 0;
		
		for (int i = start; i < end; i++) {
			final double conditionalP = histogramData[i] / (double) count;
			
			sum += conditionalP * Math.log(conditionalP);
		}
		
		return -sum;
	}
}
