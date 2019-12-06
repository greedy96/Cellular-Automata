package BoardController.board.neighbour.findNeighbour;

import BoardController.board.cells.Cell;
import BoardController.board.cells.Grain;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleNeighbourFinder implements NeighbourFinder {

    @Override
    public Grain findBestNeighbour(List<Grain> neighbourhoodGrains, int x, int y) {
        if (neighbourhoodGrains.size() == 0)
            return null;

        Map<Integer, List<Grain>> results =
                neighbourhoodGrains.stream().collect(Collectors.groupingBy(Cell::getId));

        int maxSize = results.entrySet().stream()
                .max(Map.Entry.comparingByValue((o1, o2) -> {
                    int s1 = o1.size(), s2 = o2.size();
                    return Integer.compare(s1, s2);
                })).map(entry -> entry.getValue().size()).orElse(0);

        return maxSize == 0 ? null : results.values().stream().map(max -> results.values().stream().filter(value -> value.size() == maxSize).map(result -> result.get(0)).collect(Collectors.toList()))
                .map(grainList -> grainList.size() > 1 ? grainList.get(new Random().nextInt(grainList.size())) : grainList.get(0)).findFirst().orElse(null);


//                return optional.map(Map.Entry::getValue)
//                    .map(max -> neighbourhoodGrains.entrySet().stream()
//                            .filter(entry -> Objects.equals(entry.getValue(), max))
//                            .map(Map.Entry::getKey)
//                            .collect(Collectors.toList()))
//                    .map(grainList -> grainList.size() > 1 ? grainList.get(new Random().nextInt(grainList.size())) : grainList.get(0))
//                    .orElse(null);
    }
}
