package BoardController.board.cells;

import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class Cell {

    protected int id;
    int x, y;
    protected int startStep;
    protected Color color;
    protected boolean finals = false;
    protected int cellPhase;
    protected boolean isDualPhase;

    protected Cell(int id, int x, int y, int startStep, Color color, int phase, boolean isDualPhase) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.startStep = startStep;
        this.color = color;
        this.cellPhase = phase;
        this.isDualPhase = isDualPhase;
    }

    public void setDualPhase() {
        this.isDualPhase = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return id == cell.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
