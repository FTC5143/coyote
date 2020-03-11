package com.xcentrics.coyote.geometry;

import java.util.ArrayList;
import java.util.List;

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
    
    public boolean contains(Point point) {
        return distance(point) <= radius;
    }

    public List<Point> segmentIntersections(Segment seg) {
        List<Point> intersections = new ArrayList<>();

        double bax = seg.end.x - seg.start.x;
        double bay = seg.end.y - seg.start.y;
        double cax = this.x - seg.start.x;
        double cay = this.y - seg.start.y;

        double a = bax * bax + bay * bay;
        double bby2 = bax * cax + bay * cay;
        double c = cax * cax + cay * cay - this.radius * this.radius;

        double pby2 = bby2 / a;
        double q = c / a;

        double disc = pby2 * pby2 - q;

        if (disc < 0) {
            return intersections;
        }

        double tmp_sqrt = Math.sqrt(disc);

        double ab_scaling_factor_1 = -pby2 + tmp_sqrt;
        double ab_scaling_factor_2 = -pby2 - tmp_sqrt;

        Point point_1 = new Point(
                seg.start.x - bax * ab_scaling_factor_1,
                seg.start.y - bay * ab_scaling_factor_1
        );

        intersections.add(point_1);

        if (disc == 0) {
            return intersections;
        }

        Point point_2 = new Point(
                seg.start.x - bax * ab_scaling_factor_2,
                seg.start.y - bay * ab_scaling_factor_2
        );

        intersections.add(point_2);

        return intersections;
    }
}
