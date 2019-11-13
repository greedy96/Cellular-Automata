package sample.board;

import java.util.Objects;

public class Grain {

    private int id;
    private int startStep;
    private String color;
    private Neighbourhood neighbourhood;

    public Grain(int id, int startStep, Neighbourhood neighbourhood) {
        this.id = id;
        this.startStep = startStep;
        this.neighbourhood = neighbourhood;
    }

    public int getId() {
        return id;
    }

    public int getStartStep() {
        return startStep;
    }

    public String getColor() {
        return color;
    }

    public boolean checkNeighbourhood(int positionX, int positionY) {
        return neighbourhood.getMatrix()[positionX + 1][positionY + 1];
    }

    public Grain copy(int startStep) {
        return new Grain(id, startStep, neighbourhood);
    }

    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grain grain = (Grain) o;
        return id == grain.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
