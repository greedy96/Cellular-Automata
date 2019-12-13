package BoardController;

import BoardController.board.BoardController;
import BoardController.board.NeighbourhoodEnum;
import BoardController.board.cells.Cell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    @FXML
    public BorderPane mainView;
    @FXML
    public AnchorPane controlPane;
    @FXML
    public AnchorPane startControlPane;
    @FXML
    public AnchorPane activeControlPane;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    private GridPane boardPane;

    @FXML
    public TextField rowsTextField;
    @FXML
    public TextField columnsTextField;
    @FXML
    public TextField numberOfSeeds;
    @FXML
    public TextField numberOfInclusions;
    @FXML
    public TextField minRadius;
    @FXML
    public TextField maxRadius;
    @FXML
    public RadioButton nonPeriodicBoundary;
    @FXML
    public RadioButton periodicBoundary;
    @FXML
    private ComboBox<NeighbourhoodEnum> neighbourhood;
    @FXML
    private Label boundaryCurvatureLabel;
    @FXML
    private CheckBox boundaryCurvature;
    @FXML
    private TextField thresholdValue;
    @FXML
    private Label neighbourhoodLabel;
    @FXML
    private Label thresholdLabel;

    @FXML
    private Button playButton;

    private BoardController boardController;
    private Thread autoTask = null;
    private Integer currentStep;
    private double rectangleSize;

    @FXML
    public void initialize() {
        thresholdValue.setVisible(false);
        thresholdLabel.setVisible(false);
        setChangeListener();
        neighbourhood.setItems(FXCollections.observableArrayList(NeighbourhoodEnum.values()));
        neighbourhood.setValue(NeighbourhoodEnum.VON_NEUMANN);
        ToggleGroup boundaryGroup = new ToggleGroup();
        periodicBoundary.setToggleGroup(boundaryGroup);
        nonPeriodicBoundary.setToggleGroup(boundaryGroup);
        nonPeriodicBoundary.fire();
        controlPane.getChildren().clear();
        controlPane.getChildren().setAll(startControlPane);
    }

    @FXML
    public void generateBoard() {
        try {
            int rows = parseTextToInt(rowsTextField, 1, 1000);
            int columns = parseTextToInt(columnsTextField, 1, 1000);
            int numberOfSeeds = parseTextToInt(this.numberOfSeeds, 0, Math.max(rows, columns));
            int numberOfInclusions = parseTextToInt(this.numberOfInclusions, 0, Math.max(rows, columns));
            int minRadius = parseTextToInt(this.minRadius, 0, Math.max(rows / 2, columns / 2));
            int maxRadius = parseTextToInt(this.maxRadius, minRadius, Math.max(rows / 2, columns / 2));
            if (this.boundaryCurvature.isSelected()) {
                int probability = parseTextToInt(this.thresholdValue, 1, 100);
                boardController = new BoardController(rows, columns, numberOfSeeds, numberOfInclusions, minRadius, maxRadius,
                        probability, periodicBoundary.isSelected());
            } else {
                boardController = new BoardController(rows, columns, numberOfSeeds, numberOfInclusions, minRadius, maxRadius,
                        neighbourhood.getValue(), periodicBoundary.isSelected());
            }
            currentStep = 0;
            generateBoardView(this.boardController.getMatrix());
            setBoardScale();
            controlPane.getChildren().set(0, activeControlPane);
            boardPane.setAlignment(Pos.CENTER);
        } catch (NumberFormatException ignored) {
        }
    }

    private int parseTextToInt(TextField textField, int min, int max) throws NumberFormatException {
        try {
            int number = Integer.parseInt(textField.getText());
            if (number < min || number > max)
                throw new NumberFormatException();
            textField.getStyleClass().remove("error");
            return number;
        } catch (NumberFormatException ignored) {
            textField.getStyleClass().add("error");
            throw new NumberFormatException();
        }
    }

    @FXML
    public void generateNextStep() {
        int nextStep = this.boardController.getNextStep();
        if (nextStep != currentStep) {
            currentStep = nextStep;
            changeBordView(this.boardController.getMatrix());
        }
    }

    @FXML
    public void getPreviousStep() {
        int previousStep = this.boardController.getPreviousStep();
        if (previousStep != currentStep) {
            currentStep = previousStep;
            changeBordView(this.boardController.getMatrix());
        }
    }

    @FXML
    public void goBack() {
        controlPane.getChildren().set(0, startControlPane);
        if (autoTask != null) {
            autoTask.interrupt();
            autoTask = null;
        }
    }

    private void generateBoardView(Cell[][] matrix) {
        boardPane.getChildren().clear();
        int rows = matrix.length, columns = matrix[0].length;
        rectangleSize = Math.round(Math.max(1.0, 700.0 / Math.min(rows, columns)));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                GridRectangle cell = getGrainView(matrix[i][j], i, j, rectangleSize);
                GridPane.setRowIndex(cell, i);
                GridPane.setColumnIndex(cell, j);
                boardPane.getChildren().add(cell);
            }
        }
    }

    private GridRectangle getGrainView(Cell cell, int i, int j, double size) {
        GridRectangle cellView = new GridRectangle(i, j, size);
        if (cell == null)
            cellView.setFill(Color.LIGHTGRAY);
        else
            cellView.setFill(cell.getColor());

        return cellView;
    }

    private void changeBordView(Cell[][] matrix) {
        boardPane.getChildren().forEach((rectangle) -> {
            GridRectangle gridRectangle = ((GridRectangle) rectangle);
            Cell cell = matrix[gridRectangle.getRow()][gridRectangle.getColumn()];
            if (cell != null) {
                if (cell.getStartStep() <= currentStep)
                    gridRectangle.setFill(cell.getColor());
                else
                    gridRectangle.setFill(Color.LIGHTGRAY);
            }
        });
    }

    @FXML
    public void playAuto() {
        if (autoTask == null) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    int nextStep = boardController.getNextStep();
                    while (nextStep != currentStep) {
                        currentStep = nextStep;
                        Platform.runLater(() -> changeBordView(boardController.getMatrix()));

                        nextStep = boardController.getNextStep();
                        Thread.sleep(10);
                    }
                    playButton.getStyleClass().remove("active");
                    autoTask = null;
                    return 1;
                }
            };
            playButton.getStyleClass().add("active");
            autoTask = new Thread(task);
            autoTask.start();
        }
    }

    @FXML
    public void pauseAuto() {
        if (autoTask != null) {
            playButton.getStyleClass().remove("active");
            autoTask.interrupt();
            autoTask = null;
        }

    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        Stage stage = (Stage) mainView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            boardController = new BoardController(file);
            currentStep = boardController.getCurrentStep();
            generateBoardView(this.boardController.getMatrix());
            controlPane.getChildren().set(0, activeControlPane);
            boardPane.setAlignment(Pos.CENTER);
        }
    }

    @FXML
    public void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        Stage stage = (Stage) mainView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boardController.saveToFile(file);
        }
    }

    @FXML
    public void saveAsImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("BMP image (*.bmp)", "*.bmp");
        FileChooser.ExtensionFilter extensionFilter2 = new FileChooser.ExtensionFilter("PNG image (*.png)", "*.png");
        FileChooser.ExtensionFilter extensionFilter3 = new FileChooser.ExtensionFilter("JPEG image (*.jpeg)", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.getExtensionFilters().add(extensionFilter2);
        fileChooser.getExtensionFilters().add(extensionFilter3);

        Stage stage = (Stage) mainView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boardController.generateImage(file);
        }
    }

    @FXML
    public void scaleUp() {
        rectangleSize = Math.min(70, Math.round(rectangleSize * 1.5));
        setBoardScale();
    }

    @FXML
    public void scaleDown() {
        rectangleSize = Math.max(1, Math.round(rectangleSize / 1.5));
        setBoardScale();
    }

    private void setBoardScale() {
        boardPane.getChildren().forEach((rectangle) -> {
            GridRectangle gridRectangle = ((GridRectangle) rectangle);
            gridRectangle.setHeight(rectangleSize);
            gridRectangle.setWidth(rectangleSize);
        });
    }

    private void setChangeListener() {
        boundaryCurvature.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                neighbourhood.setVisible(false);
                neighbourhoodLabel.setVisible(false);
                thresholdValue.setVisible(true);
                thresholdLabel.setVisible(true);
                boundaryCurvatureLabel.getStyleClass().add("active");
            } else {
                thresholdValue.setVisible(false);
                thresholdLabel.setVisible(false);
                neighbourhood.setVisible(true);
                neighbourhoodLabel.setVisible(true);
                boundaryCurvatureLabel.getStyleClass().remove("active");
            }
        });
    }
}
