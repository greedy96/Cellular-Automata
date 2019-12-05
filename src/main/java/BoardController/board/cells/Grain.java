package BoardController.board.cells;

import BoardController.board.Neighbourhood;
import BoardController.board.NeighbourhoodEnum;
import javafx.scene.paint.Color;

import java.util.Random;

public class Grain extends Cell {

    private NeighbourhoodEnum neighbourhoodType;
    private NeighbourhoodEnum initNeighbourhoodType;

    public Grain(int id, int startStep, Color color, NeighbourhoodEnum initNeighbourhoodType) {
        super(id, startStep, color);

        if (color == null) {
            this.color = generateColor();
        } else {
            this.color = color;
        }
        this.initNeighbourhoodType = initNeighbourhoodType;
        setNeighbourhoodType();
    }

    private Color generateColor() {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        return new Color(r, g, b, 1);
    }

    private void setNeighbourhoodType() {
        Random random = new Random();
        if (initNeighbourhoodType == NeighbourhoodEnum.HEXAGONAL_RANDOM) {
            int randomInt = random.nextInt(2);
            if (randomInt == 0)
                neighbourhoodType = NeighbourhoodEnum.HEXAGONAL_LEFT;
            else
                neighbourhoodType = NeighbourhoodEnum.HEXAGONAL_RIGHT;
        } else if (initNeighbourhoodType == NeighbourhoodEnum.PENTAGONAL_RANDOM) {
            int randomInt = random.nextInt(2);
            if (randomInt == 0)
                neighbourhoodType = NeighbourhoodEnum.PENTAGONAL_LEFT;
            else
                neighbourhoodType = NeighbourhoodEnum.PENTAGONAL_RIGHT;
        } else
            neighbourhoodType = initNeighbourhoodType;
    }

    public boolean checkNeighbourhood(int positionX, int positionY) {
        return Neighbourhood.getInstance().getNeighbourhood(neighbourhoodType)[positionX + 1][positionY + 1];
    }

    public Grain copy(int startStep) {
        return new Grain(id, startStep, color, initNeighbourhoodType);
    }

    public String toString() {
        return Integer.toString(id);
    }
}
