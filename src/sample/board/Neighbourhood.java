package sample.board;

import java.util.Random;

public class Neighbourhood {

    private NeighbourhoodEnum type;
    private boolean[][] matrix;
    private boolean[][] randomMatrix;

    Neighbourhood(NeighbourhoodEnum type) {
        this.type = type;
        this.randomMatrix = null;
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
            case MOORE:
                matrix = new boolean[][]{
                        {true, true, true},
                        {true, true, true},
                        {true, true, true}
                };
                break;
            case HEXAGONAL_LEFT:
                matrix = new boolean[][]{
                        {true, true, false},
                        {true, true, true},
                        {false, true, true}
                };
                break;
            case HEXAGONAL_RIGHT:
                matrix = new boolean[][]{
                        {false, true, true},
                        {true, true, true},
                        {true, true, false}
                };
                break;
            case HEXAGONAL_RANDOM:
                matrix = setNeighbourhoodMatrix(NeighbourhoodEnum.HEXAGONAL_LEFT);
                randomMatrix = setNeighbourhoodMatrix(NeighbourhoodEnum.HEXAGONAL_RIGHT);
                break;
            case PENTAGONAL_LEFT:
                matrix = new boolean[][]{
                        {true, true, false},
                        {true, true, false},
                        {true, true, false}
                };
                break;
            case PENTAGONAL_RIGHT:
                matrix = new boolean[][]{
                        {false, true, true},
                        {false, true, true},
                        {false, true, true}
                };
                break;
            case PENTAGONAL_RANDOM:
                matrix = setNeighbourhoodMatrix(NeighbourhoodEnum.PENTAGONAL_LEFT);
                randomMatrix = setNeighbourhoodMatrix(NeighbourhoodEnum.PENTAGONAL_RIGHT);
                break;

        }
        return matrix;
    }

    public boolean[][] getMatrix() {
        if (randomMatrix != null) {
            Random random = new Random();
            return random.nextBoolean() ? matrix : randomMatrix;
        }
        return matrix;
    }
}
