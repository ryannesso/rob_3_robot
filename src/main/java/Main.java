// Main.java
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

public class Main extends Application {
    private Pane simulationPane;
    private Group simulationGroup;

    private TextField sideInput, r1Input, r2Input, l1Input;
    private Polyline centerLine, leftLine, rightLine;
    private LineChart<Number, Number> speedChart;

    @Override
    public void start(Stage stage) {
        simulationGroup = new Group();
        simulationPane = new Pane(simulationGroup);
        simulationPane.setPrefSize(1400, 700);

        VBox root = new VBox(10);
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

        // Примерные данные скоростей (константные значения на каждом отрезке)
        int[] time = {0, 5, 10, 15, 20}; // 4 отрезка
        double[] speedsLeft = {40, 40, 40, 40};
        double[] speedsRight = {40, 40, 40, 40};

        new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane, simulationGroup).taskTwo(sideLength);
        drawSpeedChart(time, speedsLeft, speedsRight);
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

        // Пример: сначала дуга, затем прямой отрезок, затем дуга в другую сторону
        int[] time = {0, 4, 8, 12}; // каждый этап — 4 секунды
        double[] speedsLeft = {20, 40, 60}; // имитируем разные повороты
        double[] speedsRight = {60, 40, 20};

        new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane, simulationGroup).taskThree(R1, L1, R2);
        drawSpeedChart(time, speedsLeft, speedsRight);
    }

    private void runFourTask() {
        simulationGroup.getChildren().clear();
        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(400);
        robot.getRobotGroup().setLayoutY(300);
        setupLines();
        simulationGroup.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        // Пример графика при ручном управлении
        int[] time = {0, 5, 10, 15, 20};
        double[] speedsLeft = {0, 0, 0, 0, 0};   // пока пользователь не вводит команды — 0
        double[] speedsRight = {0, 0, 0, 0, 0};

        new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane, simulationGroup).taskFour(simulationPane.getScene());
        drawSpeedChart(time, speedsLeft, speedsRight);
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
        speedChart.setPrefSize(500, 300);
        speedChart.setLayoutX(850);
        speedChart.setLayoutY(30);

        XYChart.Series<Number, Number> leftSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> rightSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> centerSeries = new XYChart.Series<>();

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
