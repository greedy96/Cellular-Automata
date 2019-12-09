package BoardController.board;

import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;
import BoardController.board.cells.Inclusion;
import BoardController.board.neighbour.ProperNeighbour;
import BoardController.board.neighbour.Response;
import lombok.Getter;

import java.util.Random;

@Getter
public class Board {

    private int step, lastStep;
    private int rows, columns;
    private Cell[][] matrix;
    private NeighbourhoodEnum neighbourhoodEnum;
    ProperNeighbour properNeighbour;

    public Board(int rows, int columns, ProperNeighbour properNeighbour) {
        this.step = 0;
        this.lastStep = -1;
        this.rows = rows;
        this.columns = columns;
        this.properNeighbour = properNeighbour;
        this.matrix = new Cell[rows][columns];
        this.properNeighbour.setMatrix(matrix, rows, columns);
    }

    public void setStep(int step) {
        this.step = step;
    }

    public NeighbourhoodEnum getNeighbourhoodEnum() {
        return neighbourhoodEnum;
    }

    public void setRandomGrains(int number, NeighbourhoodEnum neighbourhoodEnum) {
        this.neighbourhoodEnum = neighbourhoodEnum;
        int i = 0;
        Random random = new Random();
        int x, y;
        while (i < number) {
            x = random.nextInt(rows);
            y = random.nextInt(columns);
            if (matrix[x][y] == null) {
                matrix[x][y] = new Grain(i, x, y, step, null, neighbourhoodEnum);
                i++;
            }
        }
    }

    public boolean generateNextStep() {
        if (step == lastStep)
            return false;
        step++;
        boolean lastStepFlag = true;

        Response response;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (matrix[row][column] == null) {
                    response = properNeighbour.getProperNeighbour(row, column, step);

                    if (response.isContinue()) {
                        if (response.getGrainResult() != null)
                            matrix[row][column] = response.getGrainResult().copy(row, column, step);
                        lastStepFlag = false;
                    }
                }
            }
        }

        if (lastStepFlag)
            lastStep = step;

        return !lastStepFlag;
    }

    public void setRandomInclusions(int numberOfInclusions, int minRadius, int maxRadius) {
        int i = 0;
        Random random = new Random();
        int x, y, radius;
        while (i < numberOfInclusions) {
            x = random.nextInt(rows);
            y = random.nextInt(columns);
            if (matrix[x][y] == null) {
                if (maxRadius - minRadius == 0)
                    radius = minRadius;
                else
                    radius = random.nextInt(maxRadius - minRadius) + minRadius;
                fillCircle(new Inclusion(i, x, y, step), x, y, radius);
                i++;
            }
        }
    }

    private void fillCircle(Inclusion inclusion, int x, int y, int r) {
        int minX = Math.max(x - r, 0), maxX = Math.min(x + r, rows - 1);
        int minY = Math.max(y - r, 0), maxY = Math.min(y + r, columns - 1);
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                if (((i - x) * (i - x) + (j - y) * (j - y)) <= (r * r)) {
                    matrix[i][j] = inclusion;
                }
            }
        }
    }
}
