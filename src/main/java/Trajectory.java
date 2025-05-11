import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;

public class Trajectory {
    private Robot robot;
    private int[] time = {1, 2, 3, 4 ,5};
    private TranslateTransition translateTransition;

    // Конструктор с передачей робота
    public Trajectory(Robot robot) {
        this.robot = robot;
        configureTransictional();
    }

    private void configureTransictional() {
        // Создаем анимацию и привязываем ее к роботу
        translateTransition = new TranslateTransition();
        translateTransition.setNode(robot.getRobotGroup()); // Привязываем к телу робота (или к другому узлу)
        translateTransition.setDuration(Duration.seconds(time[0])); // Первая продолжительность
        translateTransition.setByX(300); // Перемещение на 300 пикселей по X

        // Повторение 50 раз
        translateTransition.setCycleCount(50);
        translateTransition.setAutoReverse(false);
        System.out.println("Duration set to: " + time[0] + " seconds");
    }

    // Метод для запуска анимации с изменяемой продолжительностью
    public void startAnimation(int index) {
        if (index >= 0 && index < time.length) {
            translateTransition.setDuration(Duration.seconds(time[index]));
            System.out.println("Duration set to: " + time[index] + " seconds");
            translateTransition.play();
        }
    }

    public TranslateTransition getTranslateTransition() {
        return translateTransition;
    }
}
