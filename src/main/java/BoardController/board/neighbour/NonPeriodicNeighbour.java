package BoardController.board.neighbour;

import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;
import BoardController.board.neighbour.findNeighbour.NeighbourFinder;

import java.util.LinkedList;
import java.util.List;

public class NonPeriodicNeighbour extends ProperNeighbour {

    public NonPeriodicNeighbour(NeighbourFinder neighbourFinder) {
        super(neighbourFinder);
    }

    @Override
    public Response getProperNeighbour(int row, int column, int currentStep) {
        int minRow = Math.max(row - 1, 0), maxRow = Math.min(row + 1, rows - 1);
        int minColumn = Math.max(column - 1, 0), maxColumn = Math.min(column + 1, columns - 1);
        List<Grain> neighbourhoodGrains = new LinkedList<>();

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minColumn; j <= maxColumn; j++) {
                if (i != row || j != column) {
                    Cell currentCell = matrix[i][j];
                    if (currentCell != null && currentCell.getCellPhase() == phase && currentCell.getStartStep() != currentStep)
                        if (currentCell instanceof Grain) {
                            if (((Grain) currentCell).checkNeighbourhood(row - i, column - j)) {
                                neighbourhoodGrains.add((Grain) currentCell);
                            }
                        }
                }
            }
        }
        return neighbourFinder.findBestNeighbour(neighbourhoodGrains, row, column);
    }
}
