import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.awt.*;

import javafx.scene.paint.Color;
public class Robot {
    private Line body;
    private Rectangle leftWheel;
    private Rectangle rightWheel;
    private Group group;

    public Robot() {
        body = new Line();
        leftWheel = new Rectangle(10.0f, 12.0f, 13.0f, 0.0f);
        leftWheel.setFill(Color.rgb(0, 0, 0, 0.9));
        rightWheel = new Rectangle();
        rightWheel.setFill(Color.rgb(0, 0, 0, 0.9));


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
        group.getChildren().addAll(body, leftWheel, rightWheel);
    }

    public Group getRobotGroup() {
        return group;
    }

    public void moveRobot(double x, double y) {
        group.setLayoutX(group.getLayoutX() + x);
        group.setLayoutY(group.getLayoutY() + y);
    }

}
