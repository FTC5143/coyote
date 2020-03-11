package com.xcentrics.coyote.geometry;

import java.lang.Math;

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

    public Point copy(Point other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Point translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public double distance(Point other) {
        return Math.hypot(other.x - this.x, other.y - this.y);
    }
    
    public double angleTo(Point other) {
        return Math.atan2(other.y - this.y, other.x - this.x);
    }
}
