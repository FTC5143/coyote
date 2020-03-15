package com.xcentrics.coyote.path;

import com.xcentrics.coyote.geometry.Point;

import java.util.ArrayList;

public class PathPoint extends Point {

    public boolean passed = false;

    ArrayList<Runnable> actions = new ArrayList<>();

    public PathPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PathPoint() {
        this(0, 0);
    }

    public PathPoint(Point other) {
        this(other.x, other.y);
    }

    public PathPoint addAction(Runnable action) {
        actions.add(action);
        return this;
    }

    public void markPassed() {
        if(!passed) {
            for (Runnable action : actions) {
                action.run();
            }
        }

        passed = true;
    }
}
