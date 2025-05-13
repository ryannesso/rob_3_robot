import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.ParallelTransition;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Trajectory {
    private final Robot robot;
    private final double L = 0.1; // расстояние между колесами
    private final Polyline trajectoryLine;
    private final Polyline leftWheelLine;
    private final Polyline rightWheelLine;

    public Trajectory(Robot robot, double[] speeds, int[] time, double[] omega,
                      Polyline trajectoryLine, Polyline leftWheelLine, Polyline rightWheelLine) {
        this.robot = robot;
        this.trajectoryLine = trajectoryLine;
        this.leftWheelLine = leftWheelLine;
        this.rightWheelLine = rightWheelLine;
        configureTransitions(speeds, time, omega);
    }

    private void configureTransitions(double[] speeds, int[] time, double[] omega) {
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

                move.getKeyFrames().add(new KeyFrame(Duration.seconds(timePoint), e -> {
                    Group group = robot.getRobotGroup();

                    // Угол движения — из getRotate() с компенсацией изначального -90°
                    double angleRad = Math.toRadians(group.getRotate() - 90);
                    double dx = speedPerStep * Math.cos(angleRad);
                    double dy = speedPerStep * Math.sin(angleRad);

                    group.setLayoutX(group.getLayoutX() + dx);
                    group.setLayoutY(group.getLayoutY() + dy);

                    // Центр робота
                    double cx = group.getLayoutX();
                    double cy = group.getLayoutY();
                    trajectoryLine.getPoints().addAll(cx, cy);

                    // Положение колёс
                    Rectangle left = robot.getLeftWheel();
                    Rectangle right = robot.getRightWheel();

                    double leftX = cx + left.getTranslateX() * Math.cos(angleRad) - left.getTranslateY() * Math.sin(angleRad);
                    double leftY = cy + left.getTranslateX() * Math.sin(angleRad) + left.getTranslateY() * Math.cos(angleRad);
                    double rightX = cx + right.getTranslateX() * Math.cos(angleRad) - right.getTranslateY() * Math.sin(angleRad);
                    double rightY = cy + right.getTranslateX() * Math.sin(angleRad) + right.getTranslateY() * Math.cos(angleRad);

                    leftWheelLine.getPoints().addAll(leftX, leftY);
                    rightWheelLine.getPoints().addAll(rightX, rightY);
                }));
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
}
