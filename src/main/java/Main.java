import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        int[] time = {0, 5, 10, 15, 20};
        int[] speeds_left = {20, 0, 10, 20};
        int[] speeds_right = {20, 10, 10, -20};

        int[] distance = new int[4];
        double[] speeds = new double[4];
        double[] omega = new double[4];
        double L = 0.1;

        for(int i = 0; i < speeds_left.length; i++){
            speeds[i] = (speeds_left[i] + speeds_right[i]) / 2.0;
            omega[i] = ((speeds_right[i] - speeds_left[i]) / L);
        }

        Robot robot = new Robot();
        Polyline centerLine = new Polyline();
        centerLine.setStroke(Color.RED);

        Polyline leftLine = new Polyline();
        leftLine.setStroke(Color.BLUE);

        Polyline rightLine = new Polyline();
        rightLine.setStroke(Color.GREEN);


        Trajectory trajectory = new Trajectory(robot, speeds, time, omega, centerLine, leftLine, rightLine);

        robot.getRobotGroup().setLayoutX(400);
        robot.getRobotGroup().setLayoutY(300);


        // Создаем группу и добавляем робота и траектории
        Group root = new Group();
        root.getChildren().add(robot.getRobotGroup());
        root.getChildren().addAll(centerLine, leftLine, rightLine);


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
