package com.xcentrics.coyote.path;

import com.xcentrics.coyote.geometry.Circle;
import com.xcentrics.coyote.geometry.Point;
import com.xcentrics.coyote.geometry.Pose;
import com.xcentrics.coyote.geometry.Segment;

import java.util.ArrayList;
import java.util.List;

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

    public Circle getFollowCircle() {
        return new Circle(robot_pose.to_point(), follow_radius);
    }

    // Mark off every point we have passed as passed
    public void markPassedPoints() {
        Circle follow_circle = getFollowCircle();

        for (int i = 0; i < points.size() - 1; i++) {
            Segment seg = new Segment(points.get(i), points.get(i + 1));

            List<Point> intersections = follow_circle.segmentIntersections(seg);

            // If we only have one intersection on the line, we might be halfway over one line and halfway over another
            // Basically in that case we would be on top of a point. We will mark the point as passed once the
            // intersection on the second line is farther from the start of the segment than the circle radius
            if (intersections.size() == 1) {
                if (intersections.get(0).distance(seg.start) >= follow_radius) {
                    points.get(i).markPassed();
                }
            }
            // If our follow circle is completely on the next line, mark the start of that line as a passed point
            else if (intersections.size() == 2) {
                points.get(i).markPassed();
            }
        }
    }

    public void update(Pose new_robot_pose) {
        robot_pose.copy(new_robot_pose);

        markPassedPoints();
    }

    public Point getRecoveryPoint(RecoveryMethod method) {
        switch (method) {
            case FIRST_POINT: return points.get(0);

            case LAST_POINT: return points.get(points.size() - 1);

            case FIRST_UNPASSED_POINT: return getFirstUnpassedPoint();

            case LAST_PASSED_POINT: return getLastPassedPoint();
        }

        return null;
    }

    public Point getFollowPoint() {

        Point follow_point = null;

        Circle follow_circle = getFollowCircle();

        for (int i = 0; i < points.size() - 1; i++) {
            Segment seg = new Segment(points.get(i), points.get(i + 1));

            List<Point> intersections = follow_circle.segmentIntersections(seg);

            if (intersections.size() == 1) {
                follow_point.copy(intersections.get(0));
            }
            else if (intersections.size() == 2) {
                if (intersections.get(0).distance(seg.end) < intersections.get(1).distance(seg.end)) {
                    follow_point.copy(intersections.get(0));
                } else {
                    follow_point.copy(intersections.get(1));
                }
            }
        }

        if (follow_point == null) {
            follow_point.copy(getRecoveryPoint(recovery_method));
        }

        return follow_point;
    }

    public Pose getFollowPose() {
        Point follow_point = getFollowPoint();

        Pose follow_pose = new Pose(follow_point);

        follow_pose.angle = robot_pose.angleTo(follow_point);

        return follow_pose;
    }

    /*public boolean isComplete() {

    }*/

    public PathPoint getFirstUnpassedPoint() {
        for (PathPoint point : points) {
            if (!point.passed) {
                return point;
            }
        }
        return null;
    }

    public PathPoint getLastPassedPoint() {
        for (int i = points.size() - 1; i >= 0; i--) {
            if (points.get(i).passed) {
                return points.get(i);
            }
        }
        return null;
    }
}
