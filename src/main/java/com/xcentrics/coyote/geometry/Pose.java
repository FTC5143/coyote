package com.xcentrics.coyote.geometry;

public class Pose extends Point {

    public double angle;

    public Pose(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public Pose(double x, double y) {
        this(x, y, 0);
    }

    public Pose() {
        this(0, 0, 0);
    }

    public Pose(Pose other) {
        this(other.x, other.y, other.angle);
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
        this.angle = other.angle;
        return this;
    }

    public Point to_point() {
        return new Point(this);
    }
}
