package com.xcentrics.coyote.geometry;

import java.util.ArrayList;
import java.util.List;

public class Circle extends Point {

    public double radius;

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
    
    public boolean contains(Point point) {
        return distance(point) <= radius;
    }

    public List<Point> segmentIntersections(Segment seg) {
        List<Point> intersections = new ArrayList<>();

        Point p1 = new Point(seg.start.x - this.x, seg.start.y - this.y);
        Point p2 = new Point(seg.end.x - this.x, seg.end.y - this.y);

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;

        double d = Math.sqrt(dx * dx + dy * dy);
        double D = p1.x * p2.y - p2.x * p1.y;

        double discriminant = this.radius * this.radius * d * d - D * D;

        if (discriminant < 0) {
            return intersections;
        }

        double x1 = (D * dy + (dy >= 0 ? 1 : -1) * dx * Math.sqrt(discriminant)) / (d * d);
        double x2 = (D * dy - (dy >= 0 ? 1 : -1) * dx * Math.sqrt(discriminant)) / (d * d);

        double y1 = (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);
        double y2 = (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);

        boolean valid_intersection_1 = Math.min(p1.x, p2.x) < x1 && x1 < Math.max(p1.x, p2.x) || Math.min(p1.y, p2.y) < y1 && y1 < Math.max(p1.y, p2.y);
        boolean valid_intersection_2 = Math.min(p1.x, p2.x) < x2 && x2 < Math.max(p1.x, p2.x) || Math.min(p1.y, p2.y) < y2 && y2 < Math.max(p1.y, p2.y);

        if (valid_intersection_1) {
            intersections.add(new Point(x1 + this.x, y1 + this.y));
        }

        if (valid_intersection_2) {
            intersections.add(new Point(x2 + this.x, y2 + this.y));
        }

        return intersections;
    }
}
