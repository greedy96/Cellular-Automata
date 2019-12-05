package BoardController.board.cells;

import javafx.scene.paint.Color;
import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class Cell {

    protected int id;
    protected int startStep;
    protected Color color;

    protected Cell(int id, int startStep, Color color) {
        this.id = id;
        this.startStep = startStep;
        this.color = color;
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
