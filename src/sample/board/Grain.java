package sample.board;

import javafx.scene.paint.Color;

public class Grain extends Cell {

    private Neighbourhood neighbourhood;

    public Grain(int id, int startStep, Color color, Neighbourhood neighbourhood) {
        super(id, startStep, color);
        this.neighbourhood = neighbourhood;
    }

    public boolean checkNeighbourhood(int positionX, int positionY) {
        return neighbourhood.getMatrix()[positionX + 1][positionY + 1];
    }

    public Grain copy(int startStep) {
        return new Grain(id, startStep, color, neighbourhood);
    }


    public String toString() {
        return Integer.toString(id);
    }
}
