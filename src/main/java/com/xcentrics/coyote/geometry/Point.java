package com.xcentrics.coyote.geometry;

public class Point {

    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this(0, 0);
    }

    public Point(Point other) {
        this(other.x, other.y);
    }

    public Point clone() {
        return new Point(this);
    }

    public Point translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }
}
