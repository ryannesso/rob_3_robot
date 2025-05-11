import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.paint.Color;

public class Robot {
    private Line body;
    private Rectangle leftWheel;
    private Rectangle rightWheel;
    private Path trajectoryPath;
    private QuadCurveTo quadCurveTo;
    private Group group;

    public Robot() {
        body = new Line();
        leftWheel = new Rectangle(10.0f, 12.0f, 13.0f, 0.0f);
        leftWheel.setFill(Color.BLACK);
        rightWheel = new Rectangle(10.0f, 12.0f, 13.0f, 0.0f);
        rightWheel.setFill(Color.BLACK);

        trajectoryPath = new Path();
        trajectoryPath.setStroke(Color.BLUE);
        trajectoryPath.setStrokeWidth(2);

        // Начальная точка кривой
        trajectoryPath.getElements().add(new MoveTo(150.0, 100.0));

        quadCurveTo = new QuadCurveTo();
        quadCurveTo.setControlX(175.0);
        quadCurveTo.setControlY(50.0);
        quadCurveTo.setX(200.0);
        quadCurveTo.setY(100.0);

        trajectoryPath.getElements().add(quadCurveTo);

        body.setStartX(100.0);
        body.setStartY(100.0);
        body.setEndX(200.0);
        body.setEndY(100.0);

        leftWheel.setX(98);
        leftWheel.setY(90);
        leftWheel.setWidth(10);
        leftWheel.setHeight(20);

        rightWheel.setX(192);
        rightWheel.setY(90);
        rightWheel.setWidth(10);
        rightWheel.setHeight(20);

        group = new Group();
        group.getChildren().addAll(body, leftWheel, rightWheel, trajectoryPath);
    }

    public Group getRobotGroup() {
        return group;
    }

    public Line getBody() { return body; }
    public Rectangle getLeftWheel() { return leftWheel; }
    public Rectangle getRightWheel() { return rightWheel; }

    // Метод для обновления кривой
    public void updateQuadCurve(double controlX, double controlY, double endX, double endY) {
        quadCurveTo.setControlX(controlX);
        quadCurveTo.setControlY(controlY);
        quadCurveTo.setX(endX);
        quadCurveTo.setY(endY);
    }

    // Метод для добавления новой точки в траекторию
    public void addTrajectoryPoint(double x, double y) {
        trajectoryPath.getElements().add(new QuadCurveTo(x, y, x, y));
    }

    public void moveRobot(double x, double y) {
        group.setLayoutX(group.getLayoutX() + x);
        group.setLayoutY(group.getLayoutY() + y);
    }
}
