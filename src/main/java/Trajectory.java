import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.ParallelTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class Trajectory {
    private final Robot robot;
    private final Pane simulationPane;

    private final double L = 0.1;
    private final double SCALE = 2.0;

    private final Polyline trajectoryLine;
    private final Polyline leftWheelLine;
    private final Polyline rightWheelLine;

    public Trajectory(Robot robot, double[] speeds, int[] time, double[] omega,
                      Polyline trajectoryLine, Polyline leftWheelLine, Polyline rightWheelLine,
                      Pane simulationPane) {
        this.robot = robot;
        this.simulationPane = simulationPane;
        this.trajectoryLine = trajectoryLine;
        this.leftWheelLine = leftWheelLine;
        this.rightWheelLine = rightWheelLine;
        if (speeds != null && time != null && omega != null)
            taskOne(speeds, time, omega);
    }

    public void taskOne(double[] speeds, int[] time, double[] omega) {
        SequentialTransition sequence = new SequentialTransition();
        int segmentCount = Math.min(speeds.length, time.length - 1);

        for (int i = 0; i < segmentCount; i++) {
            double duration = time[i + 1] - time[i];
            double angleDelta = omega[i] * duration;
            double stepTime = 0.05;
            int steps = (int) (duration / stepTime);
            double speedPerStep = speeds[i] * stepTime;

            Timeline move = new Timeline();
            for (int step = 0; step < steps; step++) {
                double timePoint = step * stepTime;
                move.getKeyFrames().add(new KeyFrame(Duration.seconds(timePoint), e -> updateTrajectory(speedPerStep)));
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
    }
}
