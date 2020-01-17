package BoardController.board;

import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;
import BoardController.board.cells.Inclusion;
import BoardController.board.neighbour.NonPeriodicNeighbour;
import BoardController.board.neighbour.PeriodicNeighbour;
import BoardController.board.neighbour.ProperNeighbour;
import BoardController.board.neighbour.Response;
import BoardController.board.neighbour.findNeighbour.MooreNeighbourFinder;
import BoardController.board.neighbour.findNeighbour.NeighbourFinder;
import BoardController.board.neighbour.findNeighbour.SimpleNeighbourFinder;
import lombok.Getter;

import java.util.Random;
import java.util.Set;

@Getter
public class Board {

    private int step, lastStep;
    private int rows, columns;
    private Cell[][] matrix;
    private boolean periodicBoundary;
    private boolean curvatureBoundary = false;
    private NeighbourhoodEnum neighbourhoodEnum;
    private int probability = 100;
    private ProperNeighbour properNeighbour;
    private int phase = 0;
    int lastGrainId = 0, lastInclusionId = 0;

    public Board(int rows, int columns, boolean periodicBoundary, NeighbourhoodEnum neighbourhoodEnum) {
        this.setBoard(rows, columns);
        this.periodicBoundary = periodicBoundary;
        this.neighbourhoodEnum = neighbourhoodEnum;
        this.properNeighbour = this.getSimpleNeighbour(periodicBoundary);
        this.properNeighbour.setMatrix(matrix, rows, columns, phase);
    }

    public Board(int rows, int columns, boolean periodicBoundary, int probability) {
        this.setBoard(rows, columns);
        this.periodicBoundary = periodicBoundary;
        this.curvatureBoundary = true;
        this.probability = probability;
        this.neighbourhoodEnum = NeighbourhoodEnum.MOORE;
        this.properNeighbour = this.getMooreNeighbour(periodicBoundary, probability);
        this.properNeighbour.setMatrix(matrix, rows, columns, phase);
    }

    private void setBoard(int rows, int columns) {
        this.step = 0;
        this.lastStep = -1;
        this.rows = rows;
        this.columns = columns;
        this.matrix = new Cell[rows][columns];
    }

    private ProperNeighbour getSimpleNeighbour(boolean periodicBoundary) {
        NeighbourFinder neighbourFinder = new SimpleNeighbourFinder();
        return periodicBoundary ? new PeriodicNeighbour(neighbourFinder) : new NonPeriodicNeighbour(neighbourFinder);
    }

    private ProperNeighbour getMooreNeighbour(boolean periodicBoundary, int probability) {
        NeighbourFinder neighbourFinder = new MooreNeighbourFinder(probability);
        return periodicBoundary ? new PeriodicNeighbour(neighbourFinder) : new NonPeriodicNeighbour(neighbourFinder);
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setPhase(int phase) {
        this.phase = phase;
        if (this.properNeighbour != null) {
            this.properNeighbour.setNewPhase(phase);
        }
    }

    public NeighbourhoodEnum getNeighbourhoodEnum() {
        return neighbourhoodEnum;
    }

    public void setRandomGrains(int number) {
        int i = 0;
        Random random = new Random();
        int x, y;
        while (i < number) {
            x = random.nextInt(rows);
            y = random.nextInt(columns);
            if (matrix[x][y] == null) {
                matrix[x][y] = new Grain(lastGrainId + i, x, y, step, null, neighbourhoodEnum, phase);
                i++;
            }
        }
        lastGrainId += i + 1;
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
                fillCircle(new Inclusion(lastInclusionId + i, x, y, step, phase), x, y, radius);
                i++;
            }
        }
        lastInclusionId += i + 1;
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

    public void deleteGrains(Set<Integer> grainsIds, Set<Integer> inclusionsIds) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = matrix[i][j];
                if (cell != null) {
                    if (cell instanceof Grain) {
                        if (grainsIds.contains(cell.getId()))
                            matrix[i][j] = null;
                    } else {
                        if (inclusionsIds.contains(cell.getId())) {
                            matrix[i][j] = null;
                        }
                    }
                }
            }
        }
        this.lastStep = -1;
    }

    public void setNextPhase() {
        this.phase++;
        this.properNeighbour.setNewPhase(phase);
    }
}
