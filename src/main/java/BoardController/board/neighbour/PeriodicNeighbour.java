package BoardController.board.neighbour;

import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;
import BoardController.board.neighbour.findNeighbour.NeighbourFinder;

import java.util.LinkedList;
import java.util.List;

public class PeriodicNeighbour extends ProperNeighbour {

    public PeriodicNeighbour(NeighbourFinder neighbourFinder) {
        super(neighbourFinder);
    }

    @Override
    public Response getProperNeighbour(int row, int column, int currentStep) {
        int minRow = row - 1, maxRow = row + 1;
        int minColumn = column - 1, maxColumn = column + 1;
        List<Grain> neighbourhoodGrains = new LinkedList<>();
        int periodicRow, periodicColumn;

        for (int i = minRow; i <= maxRow; i++) {
            periodicRow = i;
            if (i < 0) periodicRow = i + rows;
            else if (i >= rows) periodicRow = i - rows;

            for (int j = minColumn; j <= maxColumn; j++) {
                periodicColumn = j;
                if (j < 0) periodicColumn = j + columns;
                else if (j >= columns) periodicColumn = j - columns;

                if (periodicRow != row || periodicColumn != column) {
                    Cell currentCell = matrix[periodicRow][periodicColumn];
                    if (currentCell != null && currentCell.getCellPhase() == phase
                            && currentCell.getStartStep() != currentStep) {
                        if (currentCell instanceof Grain) {
                            int tmpRow = Math.max(Math.min(row - periodicRow, 1), -1);
                            int tmpColumn = Math.max(Math.min(column - periodicColumn, 1), -1);

                            if (((Grain) currentCell).checkNeighbourhood(tmpRow, tmpColumn)) {
                                neighbourhoodGrains.add((Grain) currentCell);
                            }
                        }
                    }
                }
            }
        }
        return neighbourFinder.findBestNeighbour(neighbourhoodGrains, row, column);
    }
}
