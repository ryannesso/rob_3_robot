import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class Main extends Application {
    private Pane simulationPane;
    private TextField sideInput;
    private Polyline centerLine, leftLine, rightLine;
    private LineChart<Number, Number> speedChart;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        sideInput = new TextField("100");
        sideInput.setPromptText("Dĺžka strany štvorca [v metroch]");

        Button basicTaskBtn = new Button("Základná úloha");
        Button squareTaskBtn = new Button("Trajektória štvorca");

        simulationPane = new Pane();
        simulationPane.setPrefSize(1400, 700);

        basicTaskBtn.setOnAction(e -> runBasicTask());
        squareTaskBtn.setOnAction(e -> runSecondTask());

        root.getChildren().addAll(sideInput, basicTaskBtn, squareTaskBtn, simulationPane);

        Scene scene = new Scene(root);
        stage.setTitle("Simulácia robota");
        stage.setScene(scene);
        stage.show();
    }

    private void runBasicTask() {
        simulationPane.getChildren().clear();
        int[] time = {0, 5, 10, 15, 20};
        double[] speedsLeft = {20, 0, 10, 20, 10};
        double[] speedsRight = {20, 10, 10, -20, 10};
        runSimulation(time, speedsLeft, speedsRight);
    }

    private void runSecondTask() {
        simulationPane.getChildren().clear();

        double sideLength;
        try {
            sideLength = Double.parseDouble(sideInput.getText());
        } catch (NumberFormatException e) {
            sideLength = 1.0;
        }

        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(300);
        robot.getRobotGroup().setLayoutY(300);

        centerLine = new Polyline();
        leftLine = new Polyline();
        rightLine = new Polyline();

        centerLine.setStroke(Color.RED);
        leftLine.setStroke(Color.BLUE);
        rightLine.setStroke(Color.GREEN);

        centerLine.setStrokeWidth(2);
        leftLine.setStrokeWidth(2);
        rightLine.setStrokeWidth(2);

        simulationPane.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        Trajectory trajectory = new Trajectory(robot, null, null, null, centerLine, leftLine, rightLine, simulationPane);
        trajectory.taskTwo(sideLength);
    }

    private void runSimulation(int[] time, double[] speedsLeft, double[] speedsRight) {
        Robot robot = new Robot();
        robot.getRobotGroup().setLayoutX(300);
        robot.getRobotGroup().setLayoutY(300);

        centerLine = new Polyline();
        leftLine = new Polyline();
        rightLine = new Polyline();

        centerLine.setStroke(Color.RED);
        leftLine.setStroke(Color.BLUE);
        rightLine.setStroke(Color.GREEN);

        centerLine.setStrokeWidth(2);
        leftLine.setStrokeWidth(2);
        rightLine.setStrokeWidth(2);

        simulationPane.getChildren().addAll(centerLine, leftLine, rightLine, robot.getRobotGroup());

        double[] speeds = new double[speedsLeft.length];
        double[] omega = new double[speedsLeft.length];
        double L = 0.1;
        for (int i = 0; i < speedsLeft.length; i++) {
            speeds[i] = (speedsLeft[i] + speedsRight[i]) / 2.0;
            omega[i] = (speedsRight[i] - speedsLeft[i]) / L;
        }

        new Trajectory(robot, speeds, time, omega, centerLine, leftLine, rightLine, simulationPane);
        drawSpeedChart(time, speedsLeft, speedsRight);
    }

    private void drawSpeedChart(int[] time, double[] left, double[] right) {
        if (speedChart != null) simulationPane.getChildren().remove(speedChart);

        NumberAxis xAxis = new NumberAxis("Čas [s]", 0, time[time.length - 1] + 2, 1);
        NumberAxis yAxis = new NumberAxis("Rýchlosť [m/s]", -25, 25, 1);

        speedChart = new LineChart<>(xAxis, yAxis);
        speedChart.setTitle("Rýchlosti kolies a ťažiska");
        speedChart.setPrefSize(500, 300);
        speedChart.setLayoutX(850);
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

    public static void main(String[] args) {
        launch();
    }
}
