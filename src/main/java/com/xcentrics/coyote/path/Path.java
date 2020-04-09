package com.xcentrics.coyote.path;

import com.xcentrics.coyote.geometry.Circle;
import com.xcentrics.coyote.geometry.Point;
import com.xcentrics.coyote.geometry.Pose;
import com.xcentrics.coyote.geometry.Segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class Path {

    public enum RecoveryMethod {
        FIRST_UNPASSED_POINT,
        LAST_PASSED_POINT,
        LAST_POINT,
        FIRST_POINT
    }

    public enum HeadingMethod {
        TOWARDS_FOLLOW_POINT,
        AWAY_FROM_FOLLOW_POINT,
        TOWARDS_PATH_END,
        AWAY_FROM_PATH_END,
        CONSTANT_ANGLE
    }

    ArrayList<PathPoint> points = new ArrayList<>();

    Pose robot_pose = new Pose();

    double follow_radius = 5;

    double constant_heading = 0;

    double position_precision = 5;

    double heading_precision = 0.016;

    RecoveryMethod recovery_method = RecoveryMethod.FIRST_UNPASSED_POINT;

    HeadingMethod heading_method = HeadingMethod.TOWARDS_FOLLOW_POINT;

    public Path addPoint(PathPoint point) {
        points.add(point);
        return this;
    }

    public ArrayList<PathPoint> getPoints() {
        return this.points;
    }

    public Path reverse() {
        return headingMethod(HeadingMethod.AWAY_FROM_FOLLOW_POINT);
    }

    public Path followRadius(double follow_radius) {
        this.follow_radius = follow_radius;
        return this;
    }

    public Path recoveryMethod(RecoveryMethod recovery_method) {
        this.recovery_method = recovery_method;
        return this;
    }

    public Path headingMethod(HeadingMethod heading_method) {
        this.heading_method = heading_method;
        return this;
    }

    public Path constantHeading(double heading) {
        this.constant_heading = heading;

        return headingMethod(HeadingMethod.CONSTANT_ANGLE);
    }

    public Path positionPrecision(double precision) {
        this.position_precision = precision;
        return this;
    }

    public Path headingPrecision(double precision) {
        this.heading_precision = precision;
        return this;
    }

    public Circle getFollowCircle() {
        return new Circle(robot_pose.to_point(), follow_radius);
    }

    // Mark off every point we have passed as passed
    public void markPassedPoints() {
        Circle follow_circle = getFollowCircle();

        List<Double> flattened_intersections = new ArrayList<Double>();

        double flattened_current_segstart_pos = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            Segment seg = new Segment(points.get(i), points.get(i + 1));

            List<Point> intersections = follow_circle.segmentIntersections(seg);

            for (Point intersection : intersections) {
                flattened_intersections.add(flattened_current_segstart_pos + intersection.distance(seg.start));
            }

            flattened_current_segstart_pos += seg.length();
        }

        Collections.sort(flattened_intersections);

        flattened_current_segstart_pos = 0;

        for (int i = 0; i < points.size() - 1; i++) {
            Segment seg = new Segment(points.get(i), points.get(i + 1));

            if(flattened_intersections.size() == 2) {

                if((flattened_intersections.get(1) - flattened_current_segstart_pos) >= (flattened_current_segstart_pos - flattened_intersections.get(0))) {
                    points.get(i).markPassed();
                }

            } else if (flattened_intersections.size() == 1) {

                if(flattened_intersections.get(0) > (flattened_current_segstart_pos + follow_radius)) {
                    points.get(i).markPassed();
                }

            }

            flattened_current_segstart_pos += seg.length();
        }

        if(robot_pose.distance(getLastPoint()) < position_precision) {
            getLastPoint().markPassed();
        }
    }

    public void update(Pose new_robot_pose) {
        robot_pose.copy(new_robot_pose);

        markPassedPoints();
    }

    public Point getRecoveryPoint(RecoveryMethod method) {
        switch (method) {
            case FIRST_POINT: return points.get(0);

            case LAST_POINT: return getLastPoint();

            case FIRST_UNPASSED_POINT: {
                PathPoint first_unpassed_point = getFirstUnpassedPoint();
                if(first_unpassed_point == null) {
                    return getLastPoint();
                }
                return first_unpassed_point;
            }

            case LAST_PASSED_POINT: return getLastPassedPoint();
        }

        return null;
    }

    public double getHeadingGoal(HeadingMethod method, Point follow_point) {

        boolean approaching_end = getFollowCircle().contains(getLastPoint());

        switch (method) {
            case TOWARDS_FOLLOW_POINT: {
                if (approaching_end) {
                    return getSecondLastPoint().angleTo(getLastPoint());
                }
                else {
                    return robot_pose.angleTo(follow_point);
                }
            }

            case AWAY_FROM_FOLLOW_POINT: {
                if (approaching_end) {
                    return getLastPoint().angleTo(getSecondLastPoint());
                }
                else {
                    return robot_pose.angleTo(follow_point) + Math.PI;
                }
            }

            case TOWARDS_PATH_END: {
                if (approaching_end) {
                    return getSecondLastPoint().angleTo(getLastPoint());
                }
                else {
                    return robot_pose.angleTo(getLastPoint());
                }
            }

            case AWAY_FROM_PATH_END: {
                if (approaching_end) {
                    return getLastPoint().angleTo(getSecondLastPoint());
                }
                else {
                    return robot_pose.angleTo(getLastPoint()) + Math.PI;
                }
            }

            case CONSTANT_ANGLE: return constant_heading;
        }

        return 0;
    }


    public Pose getFollowPose() {

        // Start off with our follow point being the recovery point. This will be overwritten if we find actual valid intersections
        Point follow_point = getRecoveryPoint(recovery_method).clone();

        Circle follow_circle = getFollowCircle();

        for (int i = 0; i < points.size() - 1; i++) {
            Segment seg = new Segment(points.get(i), points.get(i + 1));

            List<Point> intersections = follow_circle.segmentIntersections(seg);

            if (intersections.size() == 1) {
                follow_point = intersections.get(0).clone();
            }
            else if (intersections.size() == 2) {
                if (intersections.get(0).distance(seg.end) < intersections.get(1).distance(seg.end)) {
                    follow_point = intersections.get(0).clone();
                } else {
                    follow_point = intersections.get(1).clone();
                }
            }
        }

        if (follow_circle.contains(getLastPoint())) {
            follow_point = getLastPoint().clone();
        }

        double follow_heading = getHeadingGoal(heading_method, follow_point);

        return new Pose(follow_point.x, follow_point.y, follow_heading);
    }

    public boolean isComplete() {
        return
                robot_pose.distance(getLastPoint()) < position_precision &&
                Math.abs(robot_pose.angle - getHeadingGoal(heading_method, getFollowPose())) < heading_precision;
    }

    public PathPoint getFirstUnpassedPoint() {
        for (PathPoint point : points) {
            if (!point.passed) {
                return point;
            }
        }
        return getLastPoint();
    }

    public PathPoint getLastPassedPoint() {
        for (int i = points.size() - 1; i >= 0; i--) {
            if (points.get(i).passed) {
                return points.get(i);
            }
        }
        return points.get(0);
    }

    public PathPoint getLastPoint() {
        return points.get(points.size() - 1);
    }

    public PathPoint getSecondLastPoint() {
        return points.get(points.size() - 2);
    }
}
