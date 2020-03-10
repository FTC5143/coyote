package com.xcentrics.coyote.path;

import com.xcentrics.coyote.geometry.Point;
import com.xcentrics.coyote.geometry.Pose;

import java.util.ArrayList;

public class Path {

    public enum RecoveryMethod {
        FIRST_UNPASSED_POINT,
        LAST_PASSED_POINT,
        LAST_POINT,
        FIRST_POINT
    }

    ArrayList<PathPoint> points = new ArrayList<>();

    Pose robot_pose = new Pose();

    double follow_angle = Math.PI / 2;
    double follow_radius = 5;

    RecoveryMethod recovery_method = RecoveryMethod.FIRST_UNPASSED_POINT;

    public Path addPoint(PathPoint point) {
        points.add(point);
        return this;
    }

    public Path reverse() {
        return followAngle(Math.PI / 2);
    }

    public Path followAngle(double follow_angle) {
        this.follow_angle = follow_angle;
        return this;
    }

    public Path followRadius(double follow_radius) {
        this.follow_radius = follow_radius;
        return this;
    }

    public Path recoveryMethod(RecoveryMethod recovery_method) {
        this.recovery_method = recovery_method;
        return this;
    }

    public void update(Pose new_robot_pose) {
        robot_pose.copy(new_robot_pose);
    }

    /*
    public Point getFollowPoint() {

    }
    */

    /*
    public Pose getFollowPose() {

    }
    */

    /*
    public boolean isComplete() {

    }
    */
}
