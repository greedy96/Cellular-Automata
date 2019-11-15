package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import sample.board.BoardController;
import sample.board.NeighbourhoodEnum;

public class Controller {

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

    public void generateBoard() {
        try {
            int rows = Integer.parseInt(rowsTextField.getText());
            int columns = Integer.parseInt(columnsTextField.getText());
            int numberOfSeeds = Integer.parseInt(this.numberOfSeeds.getText());
            boardController = new BoardController(rows, columns, numberOfSeeds, neighbourhood.getValue());
            boardView.getChildren().add(this.boardController.generateView());
            splitPane.getItems().remove(controlPane);
            splitPane.getItems().add(activeControlPane);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generateNextStep() {
        this.boardController.goToNextStep();
        boardView.getChildren().remove(0, boardView.getChildren().size());
        boardView.getChildren().add(this.boardController.generateView());
    }

    public void goBack() {
        splitPane.getItems().remove(activeControlPane);
        splitPane.getItems().add(controlPane);
    }
}
