package BoardController.board.neighbour.findNeighbour;

import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MooreNeighbourFinder extends SimpleNeighbourFinder {

    private int probability;

    public MooreNeighbourFinder(int probability) {
        this.probability = probability;
    }

    @Override
    public Grain findBestNeighbour(List<Grain> neighbourhoodGrains, int x, int y) {
        if (neighbourhoodGrains.size() == 0)
            return null;

        Map<Integer, List<Grain>> results =
                neighbourhoodGrains.stream().collect(Collectors.groupingBy(Cell::getId));

        Grain grain = results.values().stream().filter(grains -> grains.size() >= 5).map(grains -> grains.get(0)).findAny().orElse(null);
        if (grain == null) {
            grain = findNearestNeighbour(neighbourhoodGrains, x, y);
        }

        if (grain == null) {
            grain = findFurthestNeighbour(neighbourhoodGrains, x, y);
        }

        if (grain == null) {
            Random random = new Random();
            int randomProbability = random.nextInt(100) + 1;
            if (randomProbability <= probability) {
                return super.findBestNeighbour(neighbourhoodGrains, x, y);
            } else {
                grain = new Grain(-1, -1, -1, -1, null, null);
            }
        }
        return grain;
    }

    private Grain findNearestNeighbour(List<Grain> results, int x, int y) {
        return results.stream()
                .filter(grain -> grain.getX() == x || grain.getY() == y)
                .collect(Collectors.groupingBy(Cell::getId))
                .values().stream()
                .filter(value -> value.size() >= 3).findAny()
                .map(list -> list.get(0))
                .orElse(null);
    }

    private Grain findFurthestNeighbour(List<Grain> results, int x, int y) {
        return results.stream()
                .filter(grain -> grain.getX() != x && grain.getY() != y)
                .collect(Collectors.groupingBy(Cell::getId))
                .values().stream()
                .filter(value -> value.size() >= 3)
                .findAny().map(list -> list.get(0))
                .orElse(null);
    }
}
