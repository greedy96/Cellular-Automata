package sample.board;

import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Random;

public abstract class Cell {

    protected int id;
    protected int startStep;
    protected Color color;

    public Cell(int id, int startStep, Color color) {
        this.id = id;
        this.startStep = startStep;
        if (color == null) {
            this.color = generateColor();
        } else {
            this.color = color;
        }
    }

    public int getId() {
        return id;
    }

    public int getStartStep() {
        return startStep;
    }

    public Color getColor() {
        return color;
    }

    private Color generateColor() {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        return new Color(r, g, b, 1);
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
