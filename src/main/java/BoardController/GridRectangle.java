package BoardController;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GridRectangle extends Rectangle {

    private Integer gridId;
    private CellType cellType;
    private Color color;
    private int row, column;

    public GridRectangle(Integer gridId, CellType cellType, Color color, int row, int column, double size) {
        super(size, size);
        this.gridId = gridId;
        this.cellType = cellType;
        this.color = color;
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Color getColor() {
        return color;
    }

    public void setNewGrid(Integer gridId, CellType cellType, Color color) {
        this.gridId = gridId;
        this.cellType = cellType;
        this.color = color;
        this.setFill(color);
    }

    public enum CellType {
        GRAIN, INCLUSION, EMPTY
    }

    public boolean isProperRectangle(Integer gridId, CellType type) {
        return gridId == this.gridId && cellType == type;
    }
}
