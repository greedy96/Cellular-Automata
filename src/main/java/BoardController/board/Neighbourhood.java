package BoardController.board;

import java.util.HashMap;
import java.util.Map;

public class Neighbourhood {

    private static Neighbourhood instance;

    private Map<NeighbourhoodEnum, boolean[][]> neighbourhoods;

    private Neighbourhood() {
        neighbourhoods = new HashMap<>();
        setNeighbourhoodMap();
    }

    public static Neighbourhood getInstance() {
        if (instance == null) {
            synchronized (Neighbourhood.class) {
                if (instance == null) {
                    instance = new Neighbourhood();
                }
            }
        }
        return instance;
    }

    private void setNeighbourhoodMap() {
        neighbourhoods.put(NeighbourhoodEnum.VON_NEUMANN, new boolean[][]{
                {false, true, false},
                {true, true, true},
                {false, true, false}
        });

        neighbourhoods.put(NeighbourhoodEnum.MOORE, new boolean[][]{
                {true, true, true},
                {true, true, true},
                {true, true, true}
        });

        neighbourhoods.put(NeighbourhoodEnum.HEXAGONAL_LEFT, new boolean[][]{
                {true, true, false},
                {true, true, true},
                {false, true, true}
        });

        neighbourhoods.put(NeighbourhoodEnum.HEXAGONAL_RIGHT, new boolean[][]{
                {false, true, true},
                {true, true, true},
                {true, true, false}
        });

        neighbourhoods.put(NeighbourhoodEnum.PENTAGONAL_LEFT, new boolean[][]{
                {true, true, false},
                {true, true, false},
                {true, true, false}
        });

        neighbourhoods.put(NeighbourhoodEnum.PENTAGONAL_RIGHT, new boolean[][]{
                {false, true, true},
                {false, true, true},
                {false, true, true}
        });
    }

    public boolean[][] getNeighbourhood(NeighbourhoodEnum type) {
        return neighbourhoods.get(type);
    }
}
