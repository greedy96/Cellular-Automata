package sample;

import javafx.scene.shape.Rectangle;

public class GridRectangle extends Rectangle {

    private int row, column;

    public GridRectangle(int row, int column, double size) {
        super(size, size);
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
