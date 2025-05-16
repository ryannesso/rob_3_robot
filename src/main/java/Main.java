// Main.java
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private Pane simulationPane;
    private Group simulationGroup;

    private TextField sideInput, r1Input, r2Input, l1Input;
    private Polyline centerLine, leftLine, rightLine;
    private LineChart<Number, Number> speedChart;
    private Label realTimeSpeedLabel;

    private XYChart.Series<Number, Number> centerSeriesRealtime;
    private XYChart.Series<Number, Number> leftSeriesRealtime;
    private XYChart.Series<Number, Number> rightSeriesRealtime;
    private Timeline realtimeChartUpdater;
    private double realtimeTime = 0;

    private final double L = 0.2;

    @Override
    public void start(Stage stage) {
        simulationGroup = new Group();
        simulationPane = new Pane(simulationGroup);
        simulationPane.setPrefSize(1400, 700);

        VBox root = new VBox(10);
        realTimeSpeedLabel = new Label("Rýchlosť: 0.00 m/s");
        root.getChildren().add(realTimeSpeedLabel);
        root.setPadding(new Insets(10));

        sideInput = new TextField();
        r1Input = new TextField("100");
        r2Input = new TextField("100");
        l1Input = new TextField("100");

        Button basicTaskBtn = new Button("task one");
        Button squareTaskBtn = new Button("task two");
        Button taskThree = new Button("task three");
        Button taskFour = new Button("Task Four");

        basicTaskBtn.setOnAction(e -> runBasicTask());
        squareTaskBtn.setOnAction(e -> runSecondTask());
        taskThree.setOnAction(e -> runThreeTask());
        taskFour.setOnAction(e -> runFourTask());

        root.getChildren().addAll(
                new Label("Dĺžka strany štvorca [v metroch]"), sideInput,
                new Label("Polomer prvej krivky (R1) [mm]"), r1Input,
                new Label("Polomer druhej krivky (R2) [mm]"), r2Input,
                new Label("Dĺžka rovného úseku (L1) [mm]"), l1Input,
                basicTaskBtn, squareTaskBtn, taskThree, taskFour,
                simulationPane
        );

        Scene scene = new Scene(root);
        stage.setTitle("robot simulation");
        stage.setScene(scene);
        stage.show();

        final double[] mouseX = new double[1];
        final double[] mouseY = new double[1];

        simulationPane.setOnMousePressed(e -> {
            mouseX[0] = e.getSceneX();
            mouseY[0] = e.getSceneY();
        });

        simulationPane.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - mouseX[0];
            double dy = e.getSceneY() - mouseY[0];
            simulationGroup.setLayoutX(simulationGroup.getLayoutX() + dx);
            simulationGroup.setLayoutY(simulationGroup.getLayoutY() + dy);
            mouseX[0] = e.getSceneX();
            mouseY[0] = e.getSceneY();
        });
    }

    private void runBasicTask() {
        simulationGroup.getChildren().clear();
        int[] time = {0, 5, 10, 15, 20};
        double[] speedsLeft = {20, 0, 10, 20, 10};
        double[] speedsRight = {20, 10, 10, -20, 10};
        runSimulation(time, speedsLeft, speedsRight);
    }

    private void runSecondTask() {
        simulationGroup.getChildren().clear();
        double sideLength = parseInput(sideInput.getText(), 1.0);
        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(300);
        robot.getRobotGroup().setLayoutY(300);
        setupLines();
        simulationGroup.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        int[] time = {0, 5, 10, 15, 20};
        double[] speedsLeft = {40, 40, 40, 40};
        double[] speedsRight = {40, 40, 40, 40};

        new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane, simulationGroup).taskTwo(sideLength);
        drawSpeedChartTaskTwo(sideLength);
    }

    private void runThreeTask() {
        simulationGroup.getChildren().clear();
        double R1 = parseInput(r1Input.getText(), 100);
        double R2 = parseInput(r2Input.getText(), 100);
        double L1 = parseInput(l1Input.getText(), 100);
        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(200);
        robot.getRobotGroup().setLayoutY(500);
        setupLines();
        simulationGroup.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        int[] time = {0, 4, 8, 12};
        double[] speedsLeft = {20, 40, 60};
        double[] speedsRight = {60, 40, 20};

        new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane, simulationGroup).taskThree(R1, L1, R2);
        drawSpeedChartTaskThree(R1, L1, R2);
    }

    private void runFourTask() {
        simulationGroup.getChildren().clear();
        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(400);
        robot.getRobotGroup().setLayoutY(300);
        setupLines();
        simulationGroup.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane, simulationGroup)
                .taskFour(simulationPane.getScene(), realTimeSpeedLabel);

        startRealtimeSpeedChart(robot);
    }



    private void runSimulation(int[] time, double[] speedsLeft, double[] speedsRight) {
        simulationGroup.getChildren().clear();
        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(300);
        robot.getRobotGroup().setLayoutY(300);
        setupLines();
        simulationGroup.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        double[] speeds = new double[speedsLeft.length];
        double[] omega = new double[speedsLeft.length];
        double L = 0.1;
        for (int i = 0; i < speedsLeft.length; i++) {
            speeds[i] = (speedsLeft[i] + speedsRight[i]) / 2.0;
            omega[i] = (speedsRight[i] - speedsLeft[i]) / L;
        }

        new Trajectory(robot, speeds, time, omega, centerLine, leftLine, rightLine, simulationPane, simulationGroup);
        drawSpeedChart(time, speedsLeft, speedsRight);
    }

    private void setupLines() {
        centerLine = new Polyline();
        leftLine = new Polyline();
        rightLine = new Polyline();

        centerLine.setStroke(Color.RED);
        leftLine.setStroke(Color.BLUE);
        rightLine.setStroke(Color.GREEN);

        centerLine.setStrokeWidth(2);
        leftLine.setStrokeWidth(2);
        rightLine.setStrokeWidth(2);
    }

    private void drawSpeedChart(int[] time, double[] left, double[] right) {
        if (speedChart != null) simulationPane.getChildren().remove(speedChart);

        NumberAxis xAxis = new NumberAxis("time [s]", 0, time[time.length - 1] + 2, 1);
        NumberAxis yAxis = new NumberAxis("speed [m/s]", -25, 25, 1);
        speedChart = new LineChart<>(xAxis, yAxis);
        speedChart.setTitle("Rýchlosti kolies a ťažiska");
        speedChart.setPrefSize(700, 500);
        speedChart.setLayoutX(650);
        speedChart.setLayoutY(30);

        XYChart.Series<Number, Number> leftSeries = new XYChart.Series<>();
        leftSeries.setName("Ľavé koleso");

        XYChart.Series<Number, Number> rightSeries = new XYChart.Series<>();
        rightSeries.setName("Pravé koleso");

        XYChart.Series<Number, Number> centerSeries = new XYChart.Series<>();
        centerSeries.setName("Ťažisko");


        for (int i = 0; i < time.length - 1; i++) {
            int t1 = time[i];
            int t2 = time[i + 1];
            double vl = left[i];
            double vr = right[i];
            double vc = (vl + vr) / 2.0;

            leftSeries.getData().add(new XYChart.Data<>(t1, vl));
            leftSeries.getData().add(new XYChart.Data<>(t2, vl));

            rightSeries.getData().add(new XYChart.Data<>(t1, vr));
            rightSeries.getData().add(new XYChart.Data<>(t2, vr));

            centerSeries.getData().add(new XYChart.Data<>(t1, vc));
            centerSeries.getData().add(new XYChart.Data<>(t2, vc));
        }

        speedChart.getData().addAll(leftSeries, rightSeries, centerSeries);
        simulationPane.getChildren().add(speedChart);
    }
    private void drawSpeedChartTaskTwo(double sideLengthMeters) {
        if (speedChart != null) simulationPane.getChildren().remove(speedChart);

        double speed = 40; // [m/s]
        double stepTime = 0.05; // [s]
        double duration = sideLengthMeters / speed;
        double turnDuration = 45 * stepTime;

        int fullSections = 4;

        NumberAxis xAxis = new NumberAxis("time [s]", 0, fullSections * (duration + turnDuration), 1);
        NumberAxis yAxis = new NumberAxis("speed [m/s]", -speed - 10, speed + 10, 10);

        speedChart = new LineChart<>(xAxis, yAxis);
        speedChart.setTitle("Rýchlosti kolies a ťažiska (úloha 2)");
        speedChart.setPrefSize(700, 500);
        speedChart.setLayoutX(650);
        speedChart.setLayoutY(30);

        XYChart.Series<Number, Number> leftSeries = new XYChart.Series<>();
        leftSeries.setName("Ľavé koleso");

        XYChart.Series<Number, Number> rightSeries = new XYChart.Series<>();
        rightSeries.setName("Pravé koleso");

        XYChart.Series<Number, Number> centerSeries = new XYChart.Series<>();
        centerSeries.setName("Ťažisko");


        double t = 0;
        for (int i = 0; i < fullSections; i++) {
            // Прямолинейный участок
            leftSeries.getData().add(new XYChart.Data<>(t, speed));
            rightSeries.getData().add(new XYChart.Data<>(t, speed));
            centerSeries.getData().add(new XYChart.Data<>(t, speed));
            t += duration;
            leftSeries.getData().add(new XYChart.Data<>(t, speed));
            rightSeries.getData().add(new XYChart.Data<>(t, speed));
            centerSeries.getData().add(new XYChart.Data<>(t, speed));

            // Поворот на месте
            leftSeries.getData().add(new XYChart.Data<>(t, speed));
            rightSeries.getData().add(new XYChart.Data<>(t, -speed));
            centerSeries.getData().add(new XYChart.Data<>(t, 0));
            t += turnDuration;
            leftSeries.getData().add(new XYChart.Data<>(t, speed));
            rightSeries.getData().add(new XYChart.Data<>(t, -speed));
            centerSeries.getData().add(new XYChart.Data<>(t, 0));
        }

        speedChart.getData().addAll(leftSeries, rightSeries, centerSeries);
        simulationPane.getChildren().add(speedChart);
    }
    private void drawSpeedChartTaskThree(double R1, double L1, double R2) {
        double L = 0.2;
        if (speedChart != null) simulationPane.getChildren().remove(speedChart);

        double speed = 40;
        double stepTime = 0.05;

        double durationStraight = L1 / speed;
        double durationTurn1 = Math.PI * R1 / (2 * speed);
        double durationTurn2 = Math.PI * R2 / (2 * speed);

        NumberAxis xAxis = new NumberAxis("time [s]", 0, durationTurn1 + durationStraight + durationTurn2, 1);
        NumberAxis yAxis = new NumberAxis("speed [m/s]", -speed - 10, speed + 10, 10);

        speedChart = new LineChart<>(xAxis, yAxis);
        speedChart.setTitle("Rýchlosti kolies a ťažiska (úloha 3)");
        speedChart.setPrefSize(700, 500);
        speedChart.setLayoutX(650);
        speedChart.setLayoutY(30);

        XYChart.Series<Number, Number> leftSeries = new XYChart.Series<>();
        leftSeries.setName("Ľavé koleso");

        XYChart.Series<Number, Number> rightSeries = new XYChart.Series<>();
        rightSeries.setName("Pravé koleso");

        XYChart.Series<Number, Number> centerSeries = new XYChart.Series<>();
        centerSeries.setName("Ťažisko");


        double t = 0;

        // Первая кривая
        double vLeft1 = speed * (R1 - L / 2) / R1;
        double vRight1 = speed * (R1 + L / 2) / R1;
        leftSeries.getData().add(new XYChart.Data<>(t, vLeft1));
        rightSeries.getData().add(new XYChart.Data<>(t, vRight1));
        centerSeries.getData().add(new XYChart.Data<>(t, speed));
        t += durationTurn1;
        leftSeries.getData().add(new XYChart.Data<>(t, vLeft1));
        rightSeries.getData().add(new XYChart.Data<>(t, vRight1));
        centerSeries.getData().add(new XYChart.Data<>(t, speed));

        // Прямой участок
        leftSeries.getData().add(new XYChart.Data<>(t, speed));
        rightSeries.getData().add(new XYChart.Data<>(t, speed));
        centerSeries.getData().add(new XYChart.Data<>(t, speed));
        t += durationStraight;
        leftSeries.getData().add(new XYChart.Data<>(t, speed));
        rightSeries.getData().add(new XYChart.Data<>(t, speed));
        centerSeries.getData().add(new XYChart.Data<>(t, speed));

        // Вторая кривая
        double vLeft2 = speed * (R2 + L / 2) / R2;
        double vRight2 = speed * (R2 - L / 2) / R2;
        leftSeries.getData().add(new XYChart.Data<>(t, vLeft2));
        rightSeries.getData().add(new XYChart.Data<>(t, vRight2));
        centerSeries.getData().add(new XYChart.Data<>(t, speed));
        t += durationTurn2;
        leftSeries.getData().add(new XYChart.Data<>(t, vLeft2));
        rightSeries.getData().add(new XYChart.Data<>(t, vRight2));
        centerSeries.getData().add(new XYChart.Data<>(t, speed));

        speedChart.getData().addAll(leftSeries, rightSeries, centerSeries);
        simulationPane.getChildren().add(speedChart);
    }

    private void startRealtimeSpeedChart(Robot robot) {
        if (speedChart != null) simulationPane.getChildren().remove(speedChart);

        NumberAxis xAxis = new NumberAxis("Time [s]", 0, 100, 1);
        NumberAxis yAxis = new NumberAxis("Speed [m/s]", -50, 50, 10);

        speedChart = new LineChart<>(xAxis, yAxis);
        speedChart.setTitle("Realtime speed (Task 4)");
        speedChart.setLayoutX(850);
        speedChart.setLayoutY(30);
        speedChart.setPrefSize(500, 300);

        centerSeriesRealtime = new XYChart.Series<>();
        centerSeriesRealtime.setName("Center");

        leftSeriesRealtime = new XYChart.Series<>();
        leftSeriesRealtime.setName("Left Wheel");

        rightSeriesRealtime = new XYChart.Series<>();
        rightSeriesRealtime.setName("Right Wheel");

        speedChart.getData().addAll(centerSeriesRealtime, leftSeriesRealtime, rightSeriesRealtime);
        simulationPane.getChildren().add(speedChart);

        realtimeTime = 0;

        realtimeChartUpdater = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            double centerSpeed = robot.getLinearSpeed();
            double angularSpeed = robot.getAngularSpeed();
            double leftSpeed = centerSpeed - L / 2 * angularSpeed;
            double rightSpeed = centerSpeed + L / 2 * angularSpeed;

            centerSeriesRealtime.getData().add(new XYChart.Data<>(realtimeTime, centerSpeed));
            leftSeriesRealtime.getData().add(new XYChart.Data<>(realtimeTime, leftSpeed));
            rightSeriesRealtime.getData().add(new XYChart.Data<>(realtimeTime, rightSpeed));

            realtimeTime += 0.9;
        }));
        realtimeChartUpdater.setCycleCount(Timeline.INDEFINITE);
        realtimeChartUpdater.play();
    }



    private double parseInput(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
