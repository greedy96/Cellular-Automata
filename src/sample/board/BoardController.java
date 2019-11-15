package sample.board;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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

    public BoardController(int rows, int columns, int numberOfSeeds, NeighbourhoodEnum neighbourhoodEnum, boolean periodicBoundary) {
        board = new Board(rows, columns, periodicBoundary);
        this.periodicBoundary = periodicBoundary;
        currentStep = 0;
        board.setRandomGrains(numberOfSeeds, neighbourhoodEnum);
    }

    public AnchorPane getNextView() {
        if (currentStep == board.getLastStep())
            return null;

        if (currentStep == board.getStep()) {
            board.generateNextStep();
            currentStep = board.getStep();
            return generateView();
        }

        currentStep++;
        return generateView();
    }

    public AnchorPane getPreviousView() {
        if (currentStep == 0)
            return null;

        currentStep--;
        return generateView();
    }

    public AnchorPane getCurrentView() {
        return generateView();
    }

    private AnchorPane generateView() {
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
        if (grain == null || grain.getStartStep() > currentStep)
            grainView.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        else
            grainView.setBackground(new Background(new BackgroundFill(grain.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        return grainView;
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

                Neighbourhood neighbourhood = new Neighbourhood(neighbourhoodEnum);
                String row;
                Map<Integer, Color> colors = new HashMap<>();
                while ((row = csvReader.readLine()) != null) {
                    String[] grainString = row.split(",");
                    int x = Integer.parseInt(grainString[0]);
                    int y = Integer.parseInt(grainString[1]);
                    int id = Integer.parseInt(grainString[2]);
                    int startStep = Integer.parseInt(grainString[3]);

                    if (!colors.containsKey(id)) {
                        Grain grain = new Grain(id, startStep, null, neighbourhood);
                        board.getMatrix()[x][y] = grain;
                        colors.put(grain.getId(), grain.getColor());
                    } else {
                        board.getMatrix()[x][y] = new Grain(id, startStep, colors.get(id), neighbourhood);
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
                    Grain grain = board.getMatrix()[i][j];
                    if (grain != null) {
                        csvWriter
                                .append(String.valueOf(i))
                                .append(",")
                                .append(String.valueOf(j))
                                .append(",")
                                .append(String.valueOf(grain.getId()))
                                .append(",")
                                .append(String.valueOf(grain.getStartStep()))
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

    private void setSquareColor(BufferedImage img, int x, int y, int scale, Grain grain) {
        for (int i = x * scale; i < (x + 1) * scale; i++) {
            for (int j = y * scale; j < (y + 1) * scale; j++) {
                if (grain != null) {
                    img.setRGB(i, j, getColor(grain.getColor()));
                } else {
                    img.setRGB(i, j, getColor(Color.LIGHTGRAY));
                }
            }
        }
    }

    private int getColor(Color color) {
        return 256 * 256 * (int) (255 * color.getRed()) + 256 * (int) (255 * color.getGreen()) + (int) (255 * color.getBlue());
    }
}
