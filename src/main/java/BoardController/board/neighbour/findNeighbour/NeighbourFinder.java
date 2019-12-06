package BoardController.board.neighbour.findNeighbour;

import BoardController.board.cells.Grain;

import java.util.List;

public interface NeighbourFinder {

    Grain findBestNeighbour(List<Grain> neighbourhoodGrains, int x, int y);
}
