package sample.board;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Grain {

    private int id;
    private int startStep;
    private Paint color;
    private Neighbourhood neighbourhood;

    public Grain(int id, int startStep, Paint color, Neighbourhood neighbourhood) {
        this.id = id;
        this.startStep = startStep;
        if (color == null) {
            this.color = generateColor();
        } else {
            this.color = color;
        }
        this.neighbourhood = neighbourhood;
    }

    public int getId() {
        return id;
    }

    public int getStartStep() {
        return startStep;
    }

    public Paint getColor() {
        return color;
    }

    public boolean checkNeighbourhood(int positionX, int positionY) {
        System.out.println(Arrays.deepToString(neighbourhood.getMatrix()));
        return neighbourhood.getMatrix()[positionX + 1][positionY + 1];
    }

    public Grain copy(int startStep) {
        return new Grain(id, startStep, color, neighbourhood);
    }

    private Color generateColor() {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        return new Color(r, g, b, 1);
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
