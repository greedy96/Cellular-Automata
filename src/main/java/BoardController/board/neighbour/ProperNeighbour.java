package BoardController.board.neighbour;

import BoardController.board.cells.Cell;
import BoardController.board.neighbour.findNeighbour.NeighbourFinder;

public abstract class ProperNeighbour {

    NeighbourFinder neighbourFinder;
    int rows, columns;
    Cell[][] matrix;
    int phase;

    public ProperNeighbour(NeighbourFinder neighbourFinder) {
        this.neighbourFinder = neighbourFinder;
    }

    public void setMatrix(Cell[][] matrix, int rows, int columns, int phase) {
        this.matrix = matrix;
        this.rows = rows;
        this.columns = columns;
        this.phase = phase;
    }

    public abstract Response getProperNeighbour(int row, int column, int currentStep);

    public void setNewPhase(int phase) {
        this.phase = phase;
    }
}
