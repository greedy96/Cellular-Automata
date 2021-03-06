package BoardController.board;

import BoardController.GridRectangle;
import BoardController.SelectedItem;
import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;
import BoardController.board.cells.Inclusion;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardController {

    private Board board;
    private int currentStep;

    public BoardController(int rows, int columns, int numberOfSeeds, int numberOfInclusions, int minRadius,
                           int maxRadius, NeighbourhoodEnum neighbourhoodEnum, boolean periodicBoundary) {
        board = new Board(rows, columns, periodicBoundary, neighbourhoodEnum);
        currentStep = 0;
        board.setRandomInclusions(numberOfInclusions, minRadius, maxRadius);
        board.setRandomGrains(numberOfSeeds);
    }

    public BoardController(int rows, int columns, int numberOfSeeds, int numberOfInclusions, int minRadius,
                           int maxRadius, int probability, boolean periodicBoundary) {
        board = new Board(rows, columns, periodicBoundary, probability);
        currentStep = 0;
        board.setRandomInclusions(numberOfInclusions, minRadius, maxRadius);
        board.setRandomGrains(numberOfSeeds);
    }

    public BoardController(File file) {
        openStateFromFile(file);
    }

    public int getNextStep() {
        if (currentStep == board.getStep()) {
            board.generateNextStep();
        }

        if (currentStep != board.getLastStep()) {
            currentStep++;
        }
        return currentStep;
    }

    public int getPreviousStep() {
        if (currentStep > 0)
            currentStep--;

        return currentStep;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void openStateFromFile(File file) {
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(file));
            int currentPhase = 0;
            String firstRow = csvReader.readLine();
            if (firstRow != null) {
                String[] boardString = firstRow.split(",");
                int rows = Integer.parseInt(boardString[0]);
                int columns = Integer.parseInt(boardString[1]);
                int step = Integer.parseInt(boardString[2]);
                boolean periodicBoundary = Boolean.parseBoolean(boardString[3]);
                boolean curvatureBoundary = Boolean.parseBoolean(boardString[4]);
                NeighbourhoodEnum neighbourhoodEnum = NeighbourhoodEnum.MOORE;
                if (curvatureBoundary) {
                    int probability = Integer.parseInt(boardString[5]);
                    board = new Board(rows, columns, periodicBoundary, probability);
                } else {
                    neighbourhoodEnum = NeighbourhoodEnum.valueOf(boardString[5]);
                    board = new Board(rows, columns, periodicBoundary, neighbourhoodEnum);
                }

                board.setStep(step);
                currentStep = step;
                String row;
                Map<Integer, Color> colors = new HashMap<>();
                while ((row = csvReader.readLine()) != null) {
                    String[] grainString = row.split(",");
                    String type = grainString[0];
                    int x = Integer.parseInt(grainString[1]);
                    int y = Integer.parseInt(grainString[2]);
                    int id = Integer.parseInt(grainString[3]);
                    int startStep = Integer.parseInt(grainString[4]);
                    int phase = Integer.parseInt(grainString[5]);
                    boolean isDualPhase = Boolean.parseBoolean(grainString[6]);
                    if (phase > currentPhase) {
                        currentPhase = phase;
                    }
                    if ("G".equals(type)) {
                        if (!colors.containsKey(id)) {
                            Grain grain = new Grain(id, x, y, startStep, null, neighbourhoodEnum, phase, isDualPhase);
                            board.getMatrix()[x][y] = grain;
                            colors.put(grain.getId(), grain.getColor());
                        } else {
                            board.getMatrix()[x][y] = new Grain(id, x, y, startStep, colors.get(id), neighbourhoodEnum, phase, isDualPhase);
                        }
                    } else if ("I".equals(type)) {
                        Inclusion inclusion = new Inclusion(id, x, y, startStep, phase, isDualPhase);
                        board.getMatrix()[x][y] = inclusion;
                    }
                }
                board.setPhase(currentPhase);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToFile(File file) {
        try {
            FileWriter csvWriter = new FileWriter(file);

            csvWriter
                    .append(String.valueOf(board.getRows()))
                    .append(",")
                    .append(String.valueOf(board.getColumns()))
                    .append(",")
                    .append(String.valueOf(board.getStep()))
                    .append(",")
                    .append(String.valueOf(board.isPeriodicBoundary()))
                    .append(",")
                    .append(String.valueOf(board.isCurvatureBoundary()))
                    .append(",")
                    .append(board.isCurvatureBoundary() ? String.valueOf(board.getProbability()) : board.getNeighbourhoodEnum().name())
                    .append("\n");

            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    Cell cell = board.getMatrix()[i][j];
                    if (cell != null) {
                        if (Grain.class.equals(cell.getClass())) {
                            csvWriter.append("G,");
                        } else if (Inclusion.class.equals(cell.getClass())) {
                            csvWriter.append("I,");
                        }
                        csvWriter
                                .append(String.valueOf(i))
                                .append(",")
                                .append(String.valueOf(j))
                                .append(",")
                                .append(String.valueOf(cell.getId()))
                                .append(",")
                                .append(String.valueOf(cell.getStartStep()))
                                .append(",")
                                .append(String.valueOf(cell.getCellPhase()))
                                .append(",")
                                .append(String.valueOf(cell.isDualPhase()))
                                .append("\n");
                    }
                }
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void generateImage(File file) {
        try {
            int scale = 1400 / board.getRows();

            BufferedImage img = new BufferedImage(scale * board.getRows(), scale * board.getColumns(),
                    BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    setSquareColor(img, j, i, scale, board.getMatrix()[i][j]);
                }
            }
            String extension = "";

            int i = file.getAbsolutePath().lastIndexOf('.');
            int p = Math.max(file.getAbsolutePath().lastIndexOf('/'), file.getAbsolutePath().lastIndexOf('\\'));

            if (i > p) {
                extension = file.getAbsolutePath().substring(i + 1);
            }

            ImageIO.write(img, extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSquareColor(BufferedImage img, int x, int y, int scale, Cell cell) {
        for (int i = x * scale; i < (x + 1) * scale; i++) {
            for (int j = y * scale; j < (y + 1) * scale; j++) {
                if (cell != null) {
                    img.setRGB(i, j, getColor(cell.getColor()));
                } else {
                    img.setRGB(i, j, getColor(Color.LIGHTGRAY));
                }
            }
        }
    }

    private int getColor(Color color) {
        return 256 * 256 * (int) (255 * color.getRed()) + 256 * (int) (255 * color.getGreen()) + (int) (255 * color.getBlue());
    }

    public Cell[][] getMatrix() {
        return this.board.getMatrix();
    }

    public void setSecondGG(List<SelectedItem> selectedItemList, boolean setDP, int numberOfSeeds, int numberOfInclusions, int minRadius, int maxRadius) {
        Set<Integer> grainsSet =
                selectedItemList.stream().filter(selectedItem -> !selectedItem.onProperty().get() && selectedItem.getType() == GridRectangle.CellType.GRAIN)
                        .map(selectedItem -> selectedItem.getId()).collect(Collectors.toSet());
        Set<Integer> inclusionsSet =
                selectedItemList.stream().filter(selectedItem -> !selectedItem.onProperty().get() && selectedItem.getType() == GridRectangle.CellType.INCLUSION)
                        .map(selectedItem -> selectedItem.getId()).collect(Collectors.toSet());
        this.board.deleteGrains(grainsSet, inclusionsSet, setDP);

        this.board.setNextPhase();

        this.board.setRandomInclusions(numberOfInclusions, minRadius, maxRadius);
        this.board.setRandomGrains(numberOfSeeds);
    }

    public int getCurrentPhase() {
        return this.board.getPhase();
    }
}
