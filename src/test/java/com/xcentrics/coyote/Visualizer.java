package com.xcentrics.coyote;

import com.xcentrics.coyote.geometry.Circle;
import com.xcentrics.coyote.geometry.Point;
import com.xcentrics.coyote.geometry.Pose;
import com.xcentrics.coyote.geometry.Segment;
import com.xcentrics.coyote.path.Path;
import com.xcentrics.coyote.path.PathPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class Robot {
    Pose pose = new Pose(200, 200);
    Pose vel = new Pose();
    Pose acc = new Pose();

    final double DAMP = 0.98;
    final double DAMP_ANGLE = 0.9;

    public void update() {

        vel.x += acc.x/12;
        vel.y += acc.y/12;
        vel.angle += acc.angle/6;

        pose.x += vel.x;
        pose.y += vel.y;
        pose.angle += vel.angle;

        vel.x *= DAMP;
        vel.y *= DAMP;
        vel.angle *= DAMP_ANGLE;
    }
}

class Visualizer {
    public static void main(String[] args)
    {
        final String title = "Coyote Visualizer";
        final int width = 1800;
        final int height = width / 16 * 9;

        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        Canvas canvas = new Canvas();
        canvas.setSize(width, height);
        canvas.setBackground(Color.BLACK);
        canvas.setVisible(true);
        canvas.setFocusable(false);

        frame.add(canvas);

        canvas.createBufferStrategy(3);

        boolean running = true;

        BufferStrategy bufferStrategy;
        Graphics2D graphics;


        Path path = new Path()
                .addPoint(new PathPoint(200, 200))
                .addPoint(new PathPoint(400, 200))
                .addPoint(new PathPoint(600, 600))
                .addPoint(new PathPoint(800, 800)
                        .addAction(() -> {
                            System.out.println("halfway der hurr durr");
                        })
                )
                .addPoint(new PathPoint(1200, 700))
                .addPoint(new PathPoint(1600, 300))
                .followRadius(500)
                .headingMethod(Path.HeadingMethod.TOWARDS_FOLLOW_POINT);



        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (Exception e) {}

        Robot robot = new Robot();

        ArrayList<Point> actual_path_points = new ArrayList<>();

        while (running) {
            bufferStrategy = canvas.getBufferStrategy();
            graphics = (Graphics2D) bufferStrategy.getDrawGraphics().create();
            graphics.clearRect(0, 0, width, height);

            robot.update();
            path.update(robot.pose);

            Pose follow_pose = path.getFollowPose();
            Circle follow_circle = path.getFollowCircle();

            ArrayList<PathPoint> points = path.getPoints();

            graphics.setStroke(new BasicStroke(2));


            graphics.setColor(Color.BLUE);
            for (int i = 0; i < points.size()-1; i++) {
                Segment seg = new Segment(points.get(i), points.get(i+1));
                graphics.drawLine((int)seg.start.x, (int)seg.start.y, (int)seg.end.x, (int)seg.end.y);
            }

            graphics.setColor(Color.GREEN);
            for (int i = 0; i < points.size(); i++) {
                PathPoint point = points.get(i);

                if (point.passed) {
                    graphics.fillOval((int)point.x-8, (int)point.y-8, 16, 16);
                } else {
                    graphics.drawOval((int)point.x-8, (int)point.y-8, 16, 16);
                }
            }

            graphics.setColor(Color.PINK);
            for (Point point : actual_path_points) {
                graphics.drawLine((int)point.x, (int)point.y, (int)point.x, (int)point.y);
            }


            graphics.setColor(Color.MAGENTA);
            graphics.drawOval((int)(follow_circle.x-follow_circle.radius), (int)(follow_circle.y-follow_circle.radius), (int)follow_circle.radius*2, (int)follow_circle.radius*2);

            graphics.setColor(Color.CYAN);
            graphics.drawLine((int)follow_pose.x-5, (int)follow_pose.y-5, (int)follow_pose.x+5, (int)follow_pose.y+5);
            graphics.drawLine((int)follow_pose.x+5, (int)follow_pose.y-5, (int)follow_pose.x-5, (int)follow_pose.y+5);

            graphics.setColor(Color.ORANGE);
            graphics.drawLine((int)robot.pose.x, (int)robot.pose.y, (int)follow_pose.x, (int)follow_pose.y);

            graphics.setColor(Color.WHITE);
            Rectangle robot_rect = new Rectangle((int)robot.pose.x - 90, (int)robot.pose.y - 90, 180, 180);
            graphics.rotate(robot.pose.angle, (int) robot.pose.x, (int) robot.pose.y);
            graphics.draw(robot_rect);
            graphics.drawLine((int) robot.pose.x,(int) robot.pose.y, (int) robot.pose.x + 100,(int) robot.pose.y);

            bufferStrategy.show();
            graphics.dispose();

            robot.acc.x = Math.cos(robot.pose.angleTo(follow_pose));
            robot.acc.y = Math.sin(robot.pose.angleTo(follow_pose));
            robot.acc.angle = Math.max(Math.min((follow_pose.angle-robot.pose.angle), 0.01), -0.01);

            actual_path_points.add(robot.pose.to_point());

            try {
                TimeUnit.MILLISECONDS.sleep(8);
            } catch (Exception e) {}
        }
    }
}
