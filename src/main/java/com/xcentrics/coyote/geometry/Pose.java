package com.xcentrics.coyote.geometry;

public class Pose extends Point {

    double a;

    public Pose(double x, double y, double a) {
        this.x = x;
        this.y = y;
        this.a = a;
    }

    public Pose(double x, double y) {
        this(x, y, 0);
    }

    public Pose() {
        this(0, 0, 0);
    }

    public Pose(Pose other) {
        this(other.x, other.y, other.a);
    }

    public Pose(Point point) {
        this(point.x, point.y);
    }

    public Pose clone() {
        return new Pose(this);
    }

    public Pose copy(Pose other) {
        this.x = other.x;
        this.y = other.y;
        this.a = other.a;
        return this;
    }
}
