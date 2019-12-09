package BoardController.board.neighbour.findNeighbour;

import BoardController.board.cells.Grain;
import BoardController.board.neighbour.Response;

import java.util.List;

public interface NeighbourFinder {

    Response findBestNeighbour(List<Grain> neighbourhoodGrains, int x, int y);
}
