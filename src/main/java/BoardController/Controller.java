package BoardController;

import BoardController.board.BoardController;
import BoardController.board.NeighbourhoodEnum;
import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;
import BoardController.board.cells.Inclusion;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.HashSet;
import java.util.Set;

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
    public AnchorPane selectorPane;
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

    @FXML
    public ListView<SelectedItem> listOfSelection;
    @FXML
    public TextField numberOfSeedsDP;
    @FXML
    public TextField numberOfInclusionsDP;
    @FXML
    public TextField minRadiusDP;
    @FXML
    public TextField maxRadiusDP;
    @FXML
    public CheckBox featureDP;

    private BoardController boardController;
    private Thread autoTask = null;
    private Integer currentStep;
    private double rectangleSize;

    private void setControlPane(AnchorPane anchorPane) {
        controlPane.getChildren().clear();
        controlPane.getChildren().add(anchorPane);
    }

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
        setControlPane(startControlPane);
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
            setControlPane(activeControlPane);
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
    public void resetView() {
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
        GridRectangle cellView;
        if (cell == null) {
            cellView = new GridRectangle(null, GridRectangle.CellType.EMPTY, Color.LIGHTGRAY, i, j, size);
            cellView.setFill(Color.LIGHTGRAY);
        } else {
            Color color = cell.getColor();
            if (cell.getCellPhase() < this.boardController.getCurrentPhase()) {
                color = mixColor(cell.getColor(), Color.HOTPINK, 25);
            }
            if (cell instanceof Grain) {
                cellView = new GridRectangle(cell.getId(), GridRectangle.CellType.GRAIN, color, i, j, size);
            } else {
                cellView = new GridRectangle(cell.getId(), GridRectangle.CellType.INCLUSION, color, i, j, size);
            }
            cellView.setFill(color);
        }

        return cellView;
    }

    private void changeBordView(Cell[][] matrix) {
        boardPane.getChildren().forEach((rectangle) -> {
            GridRectangle gridRectangle = ((GridRectangle) rectangle);
            Cell cell = matrix[gridRectangle.getRow()][gridRectangle.getColumn()];
            if (cell != null) {
                if (cell.getStartStep() <= currentStep) {
                    Color color = cell.getColor();
                    if (cell.getCellPhase() < boardController.getCurrentPhase()) {
                        color = mixColor(cell.getColor(), Color.HOTPINK, 25);
                    }
                    gridRectangle.setNewGrid(cell.getId(), cell instanceof Grain ? GridRectangle.CellType.GRAIN :
                            GridRectangle.CellType.INCLUSION, color);
                } else
                    gridRectangle.setNewGrid(null, GridRectangle.CellType.EMPTY, Color.LIGHTGRAY);
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

    ChangeListener<SelectedItem> changeListener = new ChangeListener<SelectedItem>() {
        @Override
        public void changed(ObservableValue<? extends SelectedItem> observable, SelectedItem oldValue, SelectedItem newValue) {
            // Your action here
            if (newValue != null) {
                newValue.toggle();
                setNonTransparentRectangle(newValue.isOn(), newValue.getId(), newValue.getType());
            }
        }
    };

    public void goToGrainSelector() {

        Set<Grain> grains = new HashSet<>();
        Set<Inclusion> inclusions = new HashSet<>();

        Cell[][] matrix = this.boardController.getMatrix();
        int rows = matrix.length, columns = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = matrix[i][j];
                if (cell != null) {
                    if (cell instanceof Grain) {
                        grains.add((Grain) cell);
                    } else if (cell instanceof Inclusion) {
                        inclusions.add((Inclusion) cell);
                    }
                }
            }
        }

        listOfSelection.getItems().clear();

        grains.forEach(grain -> {
            listOfSelection.getItems().add(new SelectedItem(GridRectangle.CellType.GRAIN, grain.getId(), grain.getColor()));
        });

        inclusions.forEach(inclusion -> {
            listOfSelection.getItems().add(new SelectedItem(GridRectangle.CellType.INCLUSION, inclusion.getId(), inclusion.getColor()));
        });

        listOfSelection.setCellFactory((ListView<SelectedItem> l) -> new SelectedList());

        listOfSelection.getSelectionModel().selectedItemProperty().addListener(changeListener);

        setControlPane(selectorPane);
    }

    private void setNonTransparentRectangle(boolean on, int id, GridRectangle.CellType type) {
        boardPane.getChildren().forEach((rectangle) -> {
            GridRectangle gridRectangle = ((GridRectangle) rectangle);
            if (gridRectangle.isProperRectangle(id, type)) {
                if (!on) {
                    gridRectangle.setFill(mixColor(gridRectangle.getColor(), Color.LIGHTGRAY, 20));
                } else {
                    gridRectangle.setFill(gridRectangle.getColor());
                }
            }
        });
    }

    private Color mixColor(Color color1, Color color2, int percentage) {
        double r = Math.abs((color1.getRed() * percentage + color2.getRed() * (100 - percentage)) / 100);
        double g = Math.abs((color1.getGreen() * percentage + color2.getGreen() * (100 - percentage)) / 100);
        double b = Math.abs((color1.getBlue() * percentage + color2.getBlue() * (100 - percentage)) / 100);
        return new Color(r, g, b, 1.0);
    }

    @FXML
    public void goBack() {
        listOfSelection.getSelectionModel().selectedItemProperty().removeListener(changeListener);
        boardPane.getChildren().forEach((rectangle) -> {
            GridRectangle gridRectangle = ((GridRectangle) rectangle);
            gridRectangle.setFill(gridRectangle.getColor());
        });
        controlPane.getChildren().set(0, activeControlPane);
    }

    @FXML
    public void secondGG() {
        try {
            int numberOfSeeds = parseTextToInt(this.numberOfSeedsDP, 0, 100);
            int numberOfInclusions = parseTextToInt(this.numberOfInclusionsDP, 0, 20);
            int minRadius = parseTextToInt(this.minRadiusDP, 0, 20);
            int maxRadius = parseTextToInt(this.maxRadiusDP, minRadius, 20);
            this.boardController.setSecondGG(listOfSelection.getItems().subList(0, listOfSelection.getItems().size()),
                    featureDP.isSelected(), numberOfSeeds, numberOfInclusions, minRadius, maxRadius);
            listOfSelection.getSelectionModel().selectedItemProperty().removeListener(changeListener);
            generateBoardView(this.boardController.getMatrix());
            controlPane.getChildren().set(0, activeControlPane);
        } catch (NumberFormatException ignored) {
        }
    }
}
