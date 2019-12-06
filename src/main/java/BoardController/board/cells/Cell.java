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

    protected Cell(int id, int x, int y, int startStep, Color color) {
        this.id = id;
        this.x = x;
        this.y = y;
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
