import javafx.animation.RotateTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Robot robot = new Robot();
        Trajectory trajectory = new Trajectory(robot);
        trajectory.startAnimation(0);
        Group root = new Group();
        root.getChildren().add(robot.getRobotGroup());



        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String args[]){
        launch(args);
    }
}
