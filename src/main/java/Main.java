import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        int[] time = {5, 10, 15, 20};
        int[] speeds_left = {20, 0, 10, 20};
        int[] speeds_right = {20, 10, 10, -20};

        double[] speeds = new double[4];
        double[] omega = new double[4];
        double L = 0.1;

        for(int i = 0; i < speeds_left.length; i++){
            speeds[i] = (speeds_left[i] + speeds_right[i]) / 2.0;
            omega[i] = (speeds_right[i] - speeds_left[i]) / L;
        }

        Robot robot = new Robot();
        Trajectory trajectory = new Trajectory(robot, speeds, time, omega);

        // Создаем группу и добавляем робота и траектории
        Group root = new Group();
        root.getChildren().add(robot.getRobotGroup());

        // Создаем сцену и показываем окно
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Robot with Trajectories");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
