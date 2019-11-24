package sample.board;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private int step, lastStep;
    private int rows, columns;
    private Cell[][] matrix;
    private NeighbourhoodEnum neighbourhoodEnum;
    private boolean periodicBoundary;

    public Board(int rows, int columns, boolean periodicBoundary) {
        this.step = 0;
        this.lastStep = -1;
        this.rows = rows;
        this.columns = columns;
        this.periodicBoundary = periodicBoundary;
        this.matrix = new Cell[rows][columns];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getStep() {
        return step;
    }

    public int getLastStep() {
        return lastStep;
    }

    public Cell[][] getMatrix() {
        return matrix;
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
                matrix[x][y] = new Grain(i, step, null, new Neighbourhood(neighbourhoodEnum));
                i++;
            }
        }
    }

    public boolean generateNextStep() {
        if (step == lastStep)
            return false;
        step++;
        boolean lastStepFlag = true;

        Grain grain;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (matrix[row][column] == null) {
                    if (periodicBoundary)
                        grain = getPeriodicBiggestNeighbourhood(row, column);
                    else
                        grain = getBiggestNeighbourhood(row, column);

                    if (grain != null) {
                        matrix[row][column] = grain.copy(step);
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
                radius = random.nextInt(maxRadius - minRadius) + minRadius;
                fillCircle(new Inclusion(i, step, Color.BLACK), x, y, radius);
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

    private Grain getBiggestNeighbourhood(int row, int column) {
        int minRow = Math.max(row - 1, 0), maxRow = Math.min(row + 1, rows - 1);
        int minColumn = Math.max(column - 1, 0), maxColumn = Math.min(column + 1, columns - 1);
        Map<Grain, Integer> neighbourhoodGrains = new HashMap<>();

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minColumn; j <= maxColumn; j++) {
                if (i != row || j != column) {
                    if (matrix[i][j] != null && matrix[i][j].getStartStep() != step)
                        if (matrix[i][j] instanceof Grain) {
                            if (((Grain) matrix[i][j]).checkNeighbourhood(row - i, column - j)) {
                                neighbourhoodGrains.merge(((Grain) matrix[i][j]), 1, Integer::sum);
                            }
                        }
                }
            }
        }
        return getOneBiggestNeighbourhood(neighbourhoodGrains);
    }

    private Grain getOneBiggestNeighbourhood(Map<Grain, Integer> neighbourhoodGrains) {
        Grain grain = null;
        Optional<Map.Entry<Grain, Integer>> optional = neighbourhoodGrains.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
        if (optional.isPresent()) {
            int max = optional.get().getValue();
            List<Grain> grainList = neighbourhoodGrains.entrySet().stream()
                    .filter(entry -> entry.getValue() == max)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (grainList.size() > 1) {
                grain = grainList.get(new Random().nextInt(grainList.size()));
            } else {
                grain = grainList.get(0);
            }
        }
        return grain;
    }

    private Grain getPeriodicBiggestNeighbourhood(int row, int column) {
        int minRow = row - 1, maxRow = row + 1;
        int minColumn = column - 1, maxColumn = column + 1;
        Map<Grain, Integer> neighbourhoodGrains = new HashMap<>();
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
                    if (matrix[periodicRow][periodicColumn] != null && ((Grain) matrix[periodicRow][periodicColumn]).getStartStep() != step) {
                        int tmpRow = Math.max(Math.min(row - periodicRow, 1), -1);
                        int tmpColumn = Math.max(Math.min(column - periodicColumn, 1), -1);

                        if (((Grain) matrix[periodicRow][periodicColumn]).checkNeighbourhood(tmpRow, tmpColumn)) {
                            neighbourhoodGrains.merge((Grain) matrix[periodicRow][periodicColumn], 1, Integer::sum);
                        }
                    }
                }
            }
        }
        return getOneBiggestNeighbourhood(neighbourhoodGrains);
    }
}
