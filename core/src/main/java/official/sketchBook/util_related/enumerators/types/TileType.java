package official.sketchBook.util_related.enumerators.types;

public enum TileType {
    EMPTY(0, false), //bloco vazio
    BLOCK(1, true), //bloco solido
    SLOPE(2, true); //bloco diagonal

    private final int id;
    private final boolean solid;

    TileType(int id, boolean solid) {
        this.id = id;
        this.solid = solid;
    }

    public int getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public static TileType fromId(int id) {
        for (TileType type : values()) {
            if (type.id == id) return type;
        }
        return EMPTY;
    }

}
