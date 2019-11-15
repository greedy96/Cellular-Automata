package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.board.BoardController;
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
    private AnchorPane boardView;

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
            boardController = new BoardController(rows, columns, numberOfSeeds, neighbourhood.getValue());
            addBoardView(this.boardController.getCurrentView());
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
            addBoardView(view);
    }

    @FXML
    public void getPreviousStep() {
        AnchorPane view = this.boardController.getPreviousView();
        if (view != null)
            addBoardView(view);
    }

    @FXML
    public void goBack() {
        splitPane.getItems().remove(activeControlPane);
        splitPane.getItems().add(controlPane);
    }

    private void addBoardView(AnchorPane anchorPane) {
        boardView.getChildren().remove(0, boardView.getChildren().size());
        boardView.getChildren().add(anchorPane);
    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        Stage stage = (Stage) mainView.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            boardController.openStateFromFile(file);
            addBoardView(this.boardController.getCurrentView());
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
