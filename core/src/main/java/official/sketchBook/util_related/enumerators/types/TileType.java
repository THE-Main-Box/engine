package official.sketchBook.util_related.enumerators.types;

public enum TileType {
    EMPTY(0),
    SOLID(1),
    SLOPE(2); // colisão personalizada

    private final int id;

    TileType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TileType fromId(int id) {
        for (TileType type : values()) {
            if (type.id == id) return type;
        }
        return EMPTY;
    }

}
