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
        //robot group
        robotGroup = new Group();

        //body
        Line body = new Line(-10, 0, 10, 0);
        body.setStrokeWidth(4);
        body.setStroke(Color.DARKGRAY);
        Group bodyGroup = new Group(body);


        //left wheel
        leftWheel = new Rectangle(-5, -5, 10, 10);
        leftWheel.setFill(Color.BLUE);
        leftWheelGroup = new Group(leftWheel);
        leftWheelGroup.setTranslateX(-10);

        //right wheel
        rightWheel = new Rectangle(-5, -5, 10, 10);
        rightWheel.setFill(Color.RED);
        rightWheelGroup = new Group(rightWheel);
        rightWheelGroup.setTranslateX(10);


        robotGroup.getChildren().addAll(bodyGroup, leftWheelGroup, rightWheelGroup);

        robotGroup.setRotate(90);
    }


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
