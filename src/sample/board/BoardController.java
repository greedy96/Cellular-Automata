package sample.board;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class BoardController {

    private Board board;

    public BoardController(int rows, int columns, int numberOfSeeds, NeighbourhoodEnum neighbourhoodEnum) {
        board = new Board(rows, columns);
        board.setRandomGrains(numberOfSeeds, neighbourhoodEnum);
    }

    public void goToNextStep() {
        board.generateNextStep();
    }

    public AnchorPane generateView() {
        AnchorPane view = new AnchorPane();
        double size = 660.0 / this.board.getRows();

        for (int i = 0; i < this.board.getRows(); i++) {
            for (int j = 0; j < this.board.getColumns(); j++) {
                AnchorPane grain = getGrainView(this.board.getMatrix()[i][j]);
                grain.setPrefSize(size, size);
                grain.setPrefSize(size, size);
                grain.setMaxSize(size, size);
                AnchorPane.setTopAnchor(grain, i * size * 1.0);
                AnchorPane.setLeftAnchor(grain, j * size * 1.0);
                view.getChildren().add(grain);
            }
        }
        return view;
    }

    private AnchorPane getGrainView(Grain grain) {
        AnchorPane grainView = new AnchorPane();
        if (grain == null)
            grainView.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        else {
            grainView.setBackground(new Background(new BackgroundFill(grain.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        return grainView;
    }

}
