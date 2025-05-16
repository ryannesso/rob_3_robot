import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.ParallelTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class Trajectory {
    private final Robot robot;
    private final Pane simulationPane;

    private final double L = 0.2;
    private final double SCALE = 1;

    private final Polyline trajectoryLine;
    private final Polyline leftWheelLine;
    private final Polyline rightWheelLine;
    private final Group simulationGroup;


    public Trajectory(Robot robot, double[] speeds, int[] time, double[] omega,
                      Polyline trajectoryLine, Polyline leftWheelLine, Polyline rightWheelLine,
                      Pane simulationPane, Group simulationGroup) {
        this.robot = robot;
        this.simulationPane = simulationPane;
        this.simulationGroup = simulationGroup;
        this.trajectoryLine = trajectoryLine;
        this.leftWheelLine = leftWheelLine;
        this.rightWheelLine = rightWheelLine;

        if (speeds != null && time != null && omega != null)
            taskOne(speeds, time, omega);
    }


    public void taskOne(double[] speeds, int[] time, double[] omega) {
        SequentialTransition sequence = new SequentialTransition();
        int segmentCount = speeds.length;

        for (int i = 0; i < segmentCount; i++) {
            double duration;
            if (i < time.length - 1) {
                duration = time[i + 1] - time[i];
            } else {
                duration = 5;
            }

            double angleDelta = omega[i] * duration;
            double stepTime = 0.05;
            int steps = (int) (duration / stepTime);
            double speedPerStep = speeds[i] * stepTime;

            Timeline move = new Timeline();
            for (int step = 0; step < steps; step++) {
                double timePoint = step * stepTime;
                move.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(timePoint), e -> updateTrajectory(speedPerStep))
                );
            }

            move.setCycleCount(1);
            move.setDelay(Duration.ZERO);

            RotateTransition rotate = new RotateTransition(Duration.seconds(duration), robot.getRobotGroup());
            rotate.setByAngle(angleDelta);

            ParallelTransition motionPhase = new ParallelTransition(rotate, move);
            sequence.getChildren().add(new SequentialTransition(motionPhase));
        }

        sequence.play();
    }


    public void taskTwo(double sideLengthMeters) {
        SequentialTransition sequence = new SequentialTransition();

        double speed = 40;
        double stepTime = 0.05;
        double duration = sideLengthMeters / speed;
        int steps = (int) (duration / stepTime);
        double speedPerStep = speed * stepTime;

        for (int i = 0; i < 4; i++) {
            Timeline move = new Timeline();
            move.setCycleCount(steps);
            move.getKeyFrames().add(new KeyFrame(Duration.seconds(stepTime), e -> updateTrajectory(speedPerStep)));

            // плавный поворот
            int rotateSteps = 45;
            double angleStep = 90.0 / rotateSteps;
            Timeline rotate = new Timeline();
            rotate.setCycleCount(rotateSteps);
            rotate.getKeyFrames().add(new KeyFrame(Duration.seconds(stepTime), e -> {
                robot.getRobotGroup().setRotate(robot.getRobotGroup().getRotate() + angleStep);
                updateTrajectory(0); // обновляем линии
            }));

            sequence.getChildren().addAll(move, rotate);
        }

        sequence.play();
    }

    public void taskThree(double R1, double L1, double R2) {
        SequentialTransition sequence = new SequentialTransition();

        double speed = 40;
        double stepTime = 0.05;
        double speedPerStep = speed * stepTime;
        robot.getRobotGroup().setRotate(0); // Начальное направление — вверх


        // Первая кривая (левый поворот, радиус R1)
        double omega1 = speed / R1;
        int stepsCurve1 = (int) (Math.PI * R1 / 2 / (speed * stepTime)); // 90 градусов
        double angleStep1 = 90.0 / stepsCurve1;

        Timeline curve1 = new Timeline();
        curve1.setCycleCount(stepsCurve1);
        curve1.getKeyFrames().add(new KeyFrame(Duration.seconds(stepTime), e -> {
            robot.getRobotGroup().setRotate(robot.getRobotGroup().getRotate() + angleStep1);
            updateTrajectory(speedPerStep);
        }));

        // Прямой отрезок длины L1
        double duration = L1 / speed;
        int stepsStraight = (int) (duration / stepTime);

        Timeline move = new Timeline();
        move.setCycleCount(stepsStraight);
        move.getKeyFrames().add(new KeyFrame(Duration.seconds(stepTime), e -> updateTrajectory(speedPerStep)));

        // Вторая кривая (правый поворот, радиус R2)
        double omega2 = speed / R2;
        int stepsCurve2 = (int) (Math.PI * R2 / 2 / (speed * stepTime));
        double angleStep2 = 90.0 / stepsCurve2;

        Timeline curve2 = new Timeline();
        curve2.setCycleCount(stepsCurve2);
        curve2.getKeyFrames().add(new KeyFrame(Duration.seconds(stepTime), e -> {
            robot.getRobotGroup().setRotate(robot.getRobotGroup().getRotate() - angleStep2);
            updateTrajectory(speedPerStep);
        }));

        sequence.getChildren().addAll(curve1, move, curve2);
        sequence.play();
    }

    public void taskFour(Scene scene) {
        robot.getRobotGroup().setLayoutX(400);
        robot.getRobotGroup().setLayoutY(300);
        robot.getRobotGroup().setRotate(0);

        final double[] speed = {0.0};
        final double[] angularSpeed = {0.0};
        double stepTime = 0.05;

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> speed[0] = 40;
                case S -> speed[0] = -40;
                case A -> angularSpeed[0] = -90;
                case D -> angularSpeed[0] = 90;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W, S -> speed[0] = 0;
                case A, D -> angularSpeed[0] = 0;
            }
        });

        Timeline controlLoop = new Timeline(new KeyFrame(Duration.seconds(stepTime), e -> {
            double angleRad = Math.toRadians(robot.getRobotGroup().getRotate() - 90);
            double dx = speed[0] * Math.cos(angleRad) * stepTime * SCALE;
            double dy = speed[0] * Math.sin(angleRad) * stepTime * SCALE;

            robot.getRobotGroup().setRotate(robot.getRobotGroup().getRotate() + angularSpeed[0] * stepTime);
            robot.getRobotGroup().setLayoutX(robot.getRobotGroup().getLayoutX() + dx);
            robot.getRobotGroup().setLayoutY(robot.getRobotGroup().getLayoutY() + dy);

            double x = robot.getRobotGroup().getLayoutX();
            double y = robot.getRobotGroup().getLayoutY();
            trajectoryLine.getPoints().addAll(x, y);

            Point2D leftPos = simulationPane.sceneToLocal(
                    robot.getLeftWheel().localToScene(
                            robot.getLeftWheel().getWidth() / 2 - 5,
                            robot.getLeftWheel().getHeight() / 2
                    )
            );
            Point2D rightPos = simulationPane.sceneToLocal(
                    robot.getRightWheel().localToScene(
                            robot.getRightWheel().getWidth() / 2 - 5,
                            robot.getRightWheel().getHeight() / 2
                    )
            );

            leftWheelLine.getPoints().addAll(leftPos.getX(), leftPos.getY());
            rightWheelLine.getPoints().addAll(rightPos.getX(), rightPos.getY());
        }));
        controlLoop.setCycleCount(Timeline.INDEFINITE);
        controlLoop.play();
    }




    private void updateTrajectory(double speedPerStep) {
        Group robotGroup = robot.getRobotGroup();
        double angleRad = Math.toRadians(robotGroup.getRotate() - 90);

        double dx = speedPerStep * Math.cos(angleRad) * SCALE;
        double dy = speedPerStep * Math.sin(angleRad) * SCALE;
        robotGroup.setLayoutX(robotGroup.getLayoutX() + dx);
        robotGroup.setLayoutY(robotGroup.getLayoutY() + dy);

        double x = robotGroup.getLayoutX();
        double y = robotGroup.getLayoutY();
        trajectoryLine.getPoints().addAll(x, y);

        // === преобразуем обе позиции через simulationGroup.sceneToLocal(...) ===
        Point2D leftPos = simulationGroup.sceneToLocal(
                robot.getLeftWheel().localToScene(
                        robot.getLeftWheel().getWidth() / 2 - 9,
                        robot.getLeftWheel().getHeight() / 2
                )
        );

        Point2D rightPos = simulationGroup.sceneToLocal(
                robot.getRightWheel().localToScene(
                        robot.getRightWheel().getWidth() / 2,
                        robot.getRightWheel().getHeight() / 2
                )
        );

        leftWheelLine.getPoints().addAll(leftPos.getX(), leftPos.getY());
        rightWheelLine.getPoints().addAll(rightPos.getX(), rightPos.getY());
    }


}
