package sample.board;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private int step, lastStep;
    private int rows, columns;
    private Grain[][] matrix;
    private NeighbourhoodEnum neighbourhoodEnum;

    public Board(int rows, int columns) {
        this.step = 0;
        this.lastStep = -1;
        this.rows = rows;
        this.columns = columns;
        this.matrix = new Grain[rows][columns];
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

    public Grain[][] getMatrix() {
        return matrix;
    }

    public void setStep(int step) {
        this.step = step;
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

    private Grain getBiggestNeighbourhood(int row, int column) {
        int minRow = Math.max(row - 1, 0), maxRow = Math.min(row + 1, rows - 1);
        int minColumn = Math.max(column - 1, 0), maxColumn = Math.min(column + 1, columns - 1);
        Grain grain = null;
        Map<Grain, Integer> neighbourhoodGrains = new HashMap<>();

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minColumn; j <= maxColumn; j++) {
                if (i != row || j != column) {
                    if (matrix[i][j] != null && matrix[i][j].getStartStep() != step) {
                        if (matrix[i][j].checkNeighbourhood(row - i, column - j)) {
                            neighbourhoodGrains.merge(matrix[i][j], 1, Integer::sum);
                        }
                    }
                }
            }
        }

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

    public NeighbourhoodEnum getNeighbourhoodEnum() {
        return neighbourhoodEnum;
    }
}
