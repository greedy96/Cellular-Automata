package sample.board;

public class Neighbourhood {

    private NeighbourhoodEnum type;
    private boolean[][] matrix;

    Neighbourhood(NeighbourhoodEnum type) {
        this.type = type;
        this.matrix = setNeighbourhoodMatrix(type);
        if (this.matrix == null)
            this.matrix = new boolean[3][3];
    }

    private boolean[][] setNeighbourhoodMatrix(NeighbourhoodEnum type) {
        boolean[][] matrix = null;
        switch (type) {
            case VON_NEUMANN:
                matrix = new boolean[][]{
                        {false, true, false},
                        {true, true, true},
                        {false, true, false}
                };
                break;
        }
        return matrix;
    }

    public boolean[][] getMatrix() {
        return matrix;
    }
}
