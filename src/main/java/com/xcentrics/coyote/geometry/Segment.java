package com.xcentrics.coyote.geometry;

public class Segment {

    public Point start;
    public Point end;

    public Segment(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Segment(Segment other) {
        this(other.start.clone(), other.end.clone());
    }

    public Segment clone() {
        return new Segment(this);
    }
    
    public double length() {
        return start.distance(end);
    }
    
    public double angle() {
        return start.angleTo(end);
    }
}
