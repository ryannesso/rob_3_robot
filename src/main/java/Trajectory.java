import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Group;

public class Trajectory {
    private Robot robot;

    // Расстояние между колесами робота
    private final double L = 20.0; // Подберите точное значение для вашего робота

    // Конструктор с передачей робота
    public Trajectory(Robot robot, double[] speeds, int[] time, double[] omega) {
        this.robot = robot;
        configureTransictional(speeds, time, omega);
    }

    // Метод настройки анимации
    public void configureTransictional(double[] speeds, int[] time, double[] omega) {
        startParallelTransition(speeds, time, omega, 0);
    }

    // Метод запуска параллельной анимации
    private void startParallelTransition(double[] speeds, int[] time, double[] omega, int index) {
        if (index >= 0 && index < time.length) {
            // Создаем анимации перемещения и вращения
            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setNode(robot.getRobotGroup());
            translateTransition.setByX(speeds[index] * time[index]);
            translateTransition.setDuration(Duration.seconds(time[index]));

            RotateTransition rotateTransition = new RotateTransition();
            rotateTransition.setNode(robot.getRobotGroup());
            rotateTransition.setByAngle(omega[index] * time[index]);
            rotateTransition.setDuration(Duration.seconds(time[index]));

            // Создаем параллельную анимацию
            ParallelTransition parallelTransition = new ParallelTransition(translateTransition, rotateTransition);

            // При завершении запускаем следующую фазу
            parallelTransition.setOnFinished(e -> startParallelTransition(speeds, time, omega, index + 1));
            parallelTransition.play();
        }
    }
}
