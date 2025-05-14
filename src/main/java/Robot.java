import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Robot {
    private final Group robotGroup;
    private final Group leftWheelGroup;
    private final Group rightWheelGroup;

    private final Rectangle leftWheel;
    private final Rectangle rightWheel;

    public Robot() {
        // Основная группа для всего робота
        robotGroup = new Group();

        // === ТЕЛО ===
        Line body = new Line(-50, 0, 50, 0); // длина 100 пикселей
        body.setStrokeWidth(4);
        body.setStroke(Color.DARKGRAY);
        Group bodyGroup = new Group(body);

        // === ЛЕВОЕ КОЛЕСО ===
        leftWheel = new Rectangle(-5, -10, 10, 20); // ширина 10, высота 20
        leftWheel.setFill(Color.BLUE);
        leftWheelGroup = new Group(leftWheel);
        leftWheelGroup.setTranslateX(-50); // левый край тела

        // === ПРАВОЕ КОЛЕСО ===
        rightWheel = new Rectangle(-5, -10, 10, 20);
        rightWheel.setFill(Color.RED);
        rightWheelGroup = new Group(rightWheel);
        rightWheelGroup.setTranslateX(50); // правый край тела

        // === СБОРКА РОБОТА ===
        robotGroup.getChildren().addAll(bodyGroup, leftWheelGroup, rightWheelGroup);

        // Начальная ориентация — вперёд по оси X
        robotGroup.setRotate(90);
    }

    // === Геттеры ===

    public Group getRobotGroup() {
        return robotGroup;
    }

    public Group getLeftWheelGroup() {
        return leftWheelGroup;
    }

    public Group getRightWheelGroup() {
        return rightWheelGroup;
    }

    public Rectangle getLeftWheel() {
        return leftWheel;
    }

    public Rectangle getRightWheel() {
        return rightWheel;
    }
}
