package sample.board;

import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BoardController {

    private Board board;
    private int currentStep;
    private boolean periodicBoundary;

    public BoardController(int rows, int columns, int numberOfSeeds, int numberOfInclusions, int minRadius,
                           int maxRadius, NeighbourhoodEnum neighbourhoodEnum, boolean periodicBoundary) {
        board = new Board(rows, columns, periodicBoundary);
        this.periodicBoundary = periodicBoundary;
        currentStep = 0;
        board.setRandomInclusions(numberOfInclusions, minRadius, maxRadius);
        board.setRandomGrains(numberOfSeeds, neighbourhoodEnum);
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

            String firstRow = csvReader.readLine();
            if (firstRow != null) {
                String[] boardString = firstRow.split(",");
                int rows = Integer.parseInt(boardString[0]);
                int columns = Integer.parseInt(boardString[1]);
                int step = Integer.parseInt(boardString[2]);
                NeighbourhoodEnum neighbourhoodEnum = NeighbourhoodEnum.valueOf(boardString[3]);

                board = new Board(rows, columns, periodicBoundary);
                board.setStep(step);
                currentStep = step;
                Neighbourhood neighbourhood = new Neighbourhood(neighbourhoodEnum);
                String row;
                Map<Integer, Color> colors = new HashMap<>();
                while ((row = csvReader.readLine()) != null) {
                    String[] grainString = row.split(",");
                    String type = grainString[0];
                    int x = Integer.parseInt(grainString[1]);
                    int y = Integer.parseInt(grainString[2]);
                    int id = Integer.parseInt(grainString[3]);
                    int startStep = Integer.parseInt(grainString[4]);
                    if ("G".equals(type)) {
                        if (!colors.containsKey(id)) {
                            Grain grain = new Grain(id, startStep, null, neighbourhood);
                            board.getMatrix()[x][y] = grain;
                            colors.put(grain.getId(), grain.getColor());
                        } else {
                            board.getMatrix()[x][y] = new Grain(id, startStep, colors.get(id), neighbourhood);
                        }
                    } else if ("I".equals(type)) {
                        Inclusion inclusion = new Inclusion(id, startStep);
                        board.getMatrix()[x][y] = inclusion;
                    }
                }
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
                    .append(board.getNeighbourhoodEnum().name())
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
            int scale = 640 / board.getRows();

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
}
