import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Robot {
    private Group group;
    private Rectangle leftWheel, rightWheel;

    public Robot() {
        group = new Group();

        // Центр тела — (0, 0), вытянуто по X (ширина = 100)
        Line body = new Line(-50, 0, 50, 0);

        // Колёса — по краям тела
        leftWheel = new Rectangle(-52, -10, 10, 20);  // левое колесо
        rightWheel = new Rectangle(42, -10, 10, 20);  // правое колесо

        leftWheel.setFill(Color.BLACK);
        rightWheel.setFill(Color.BLACK);

        group.getChildren().addAll(body, leftWheel, rightWheel);

        // ВНИМАНИЕ: не нужен setTranslate — модель уже центрирована
        // Поворачиваем визуально, чтобы “вперёд” было вправо (+X)
        group.setRotate(90); // потому что изначально тело вдоль X
    }

    public Group getRobotGroup() {
        return group;
    }

    public Rectangle getLeftWheel() {
        return leftWheel;
    }
    public Rectangle getRightWheel() {
        return rightWheel;
    }
}
