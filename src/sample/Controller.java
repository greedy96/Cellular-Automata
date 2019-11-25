package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.board.BoardController;
import sample.board.Cell;
import sample.board.NeighbourhoodEnum;

import java.io.File;

public class Controller {

    @FXML
    public GridPane mainView;
    @FXML
    public SplitPane splitPane;
    @FXML
    public AnchorPane controlPane;
    @FXML
    public AnchorPane activeControlPane;

    @FXML
    public TextField rowsTextField;
    @FXML
    public TextField columnsTextField;
    @FXML
    public TextField numberOfSeeds;
    @FXML
    public RadioButton nonPeriodicBoundary;
    @FXML
    public RadioButton periodicBoundary;
    @FXML
    private BoardController boardController;

    @FXML
    private ComboBox<NeighbourhoodEnum> neighbourhood;

    @FXML
    private GridPane boardView;

    private Thread autoTask = null;

    @FXML
    public TextField numberOfInclusions;
    @FXML
    public TextField minRadius;
    @FXML
    public TextField maxRadius;

    @FXML
    public void initialize() {
        neighbourhood.setItems(FXCollections.observableArrayList(NeighbourhoodEnum.values()));
        neighbourhood.setValue(NeighbourhoodEnum.VON_NEUMANN);
        ToggleGroup boundaryGroup = new ToggleGroup();
        periodicBoundary.setToggleGroup(boundaryGroup);
        nonPeriodicBoundary.setToggleGroup(boundaryGroup);
        nonPeriodicBoundary.fire();
        splitPane.getItems().remove(activeControlPane);
    }

    @FXML
    public void generateBoard() {
        try {
            int rows = Integer.parseInt(rowsTextField.getText());
            int columns = Integer.parseInt(columnsTextField.getText());
            int numberOfSeeds = Integer.parseInt(this.numberOfSeeds.getText());
            int numberOfInclusions = Integer.parseInt(this.numberOfInclusions.getText());
            int minRadius = Integer.parseInt(this.minRadius.getText());
            int maxRadius = Integer.parseInt(this.maxRadius.getText());
            boardController = new BoardController(rows, columns, numberOfSeeds, numberOfInclusions, minRadius, maxRadius,
                    neighbourhood.getValue(), periodicBoundary.isSelected());
            generateBoardView(this.boardController.getMatrix());
            splitPane.getItems().remove(controlPane);
            splitPane.getItems().add(activeControlPane);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generateNextStep() {
        AnchorPane view = this.boardController.getNextView();
        if (view != null)
            changeBordView(this.boardController.getMatrix());
    }

    @FXML
    public void getPreviousStep() {
        AnchorPane view = this.boardController.getPreviousView();
        if (view != null)
            changeBordView(this.boardController.getMatrix());
    }

    @FXML
    public void goBack() {
        splitPane.getItems().remove(activeControlPane);
        splitPane.getItems().add(controlPane);
        if (autoTask != null) {
            autoTask.interrupt();
            autoTask = null;
        }
    }

    private void generateBoardView(sample.board.Cell[][] matrix) {
        boardView.getChildren().clear();
        int rows = matrix.length, columns = matrix[0].length;
        double size = 660.0 / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                GridRectangle cell = getGrainView(matrix[i][j], i, j, size);
                GridPane.setRowIndex(cell, i);
                GridPane.setColumnIndex(cell, j);
                boardView.getChildren().add(cell);
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

    private void changeBordView(sample.board.Cell[][] matrix) {
        boardView.getChildren().forEach((rectangle) -> {
            GridRectangle gridRectangle = ((GridRectangle) rectangle);
            Cell cell = matrix[gridRectangle.getRow()][gridRectangle.getColumn()];
            if (cell != null) {
                gridRectangle.setFill(cell.getColor());
            }
        });
    }

    @FXML
    public void playAuto() {
        if (autoTask == null) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    AnchorPane view = boardController.getNextView();
                    while (view != null) {
                        final AnchorPane viewTMP = view;
                        Platform.runLater(() -> changeBordView(boardController.getMatrix()));

                        Thread.sleep(1000);
                        view = boardController.getNextView();
                    }
                    autoTask = null;
                    return 1;
                }
            };

            autoTask = new Thread(task);
            autoTask.start();
        }
    }

    @FXML
    public void pauseAuto() {
        if (autoTask != null) {
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
            boardController.openStateFromFile(file);
//            addBoardView(this.boardController.getCurrentView());
            splitPane.getItems().remove(controlPane);
            splitPane.getItems().add(activeControlPane);
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
        FileChooser.ExtensionFilter extensionFilter3 = new FileChooser.ExtensionFilter("JPEQ image (*.jpeg)", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.getExtensionFilters().add(extensionFilter2);
        fileChooser.getExtensionFilters().add(extensionFilter3);

        Stage stage = (Stage) mainView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boardController.generateImage(file);
        }
    }
}
