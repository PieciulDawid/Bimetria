package com.example.zadanie1;

import lombok.Getter;


@Getter
public final class Point {
	
	private final int x;
	
	private final int y;
	
	
	private Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Point of(int x, int y) {
		return new Point(x, y);
	}
	
	public Point translatedBy(int x, int y) {
		return Point.of(this.x + x, this.y + y);
	}
}
