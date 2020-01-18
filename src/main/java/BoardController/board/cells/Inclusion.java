package BoardController.board.cells;

import javafx.scene.paint.Color;

public class Inclusion extends Cell {

    public Inclusion(int id, int x, int y, int startStep, int phase, boolean isDualPhase) {
        super(id, x, y, startStep, Color.BLACK, phase, isDualPhase);
    }
}
