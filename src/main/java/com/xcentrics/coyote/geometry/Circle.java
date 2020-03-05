package com.xcentrics.coyote.geometry;

public class Circle extends Point {

    double radius;

    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Circle(Circle other) {
        this(other.x, other.y, other.radius);
    }

    public Circle(Point point, double radius) {
        this(point.x, point.y, radius);
    }

    public Circle clone() {
        return new Circle(this);
    }
    
    public Circle contains(Point point) {
        return distance(point) <= radius;
    }
}
