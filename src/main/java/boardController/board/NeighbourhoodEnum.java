package boardController.board;

public enum NeighbourhoodEnum {
    VON_NEUMANN("Von Neumann"), MOORE("Moore"), HEXAGONAL_LEFT("Hexagonal left"),
    HEXAGONAL_RIGHT("Hexagonal right"), HEXAGONAL_RANDOM("Hexagonal random"), PENTAGONAL_LEFT("Pentagonal left"),
    PENTAGONAL_RIGHT("Pentagonal right"), PENTAGONAL_RANDOM("Pentagonal random");

    private String name;

    NeighbourhoodEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
