package ru.gafi.common;

/**
 * User: Michael
 * Date: 20.05.13
 * Time: 21:30
 */
public class Point {

	public int x, y;

	public Point() {
		this(0, 0);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Point)) return false;

		Point point = (Point) o;

		if (x != point.x) return false;
		if (y != point.y) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (x * 397) ^ y;
	}

	public void set(Point point) {
		set(point.x, point.y);
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
