package com.xcentrics.coyote.path;

import com.xcentrics.coyote.geometry.Point;

import java.util.ArrayList;

public class PathPoint extends Point {

    boolean passed = false;

    ArrayList<Runnable> actions = new ArrayList<>();

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
