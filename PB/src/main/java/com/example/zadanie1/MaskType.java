package com.example.zadanie1;

enum MaskType {
	SOBEL {
		@Override
		int[][] getA() {
			return new int[][] {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
		}
		
		@Override
		int[][] getB() {
			return new int[][] {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
		}
		
	}, PREWITT {
		@Override
		int[][] getA() {
			return new int[][] {{1, 1, 1}, {0, 0, 0}, {-1, -1, -1}};
		}
		
		@Override
		int[][] getB() {
			return new int[][] {{1, 0, -1}, {1, 0, -1}, {1, 0, -1}};
		}
		
	};
	
	abstract int[][] getA();
	
	abstract int[][] getB();
	
}