package official.sketchBook.game.util_related.enumerators.types;

public enum TileType {
    EMPTY(0, false, 0f, 0, 0), //bloco vazio
    BLOCK(1, true, 0.8f, 1, 0);//bloco existente

    private final int id;
    private final boolean solid;
    private final float friction;
    private final float density;
    private final float restituition;

    TileType(int id, boolean solid, float friction, float density, float restituition) {
        this.id = id;
        this.solid = solid;
        this.friction = friction;
        this.density = density;
        this.restituition = restituition;
    }

    public float getDensity() {
        return density;
    }

    public float getRestituition() {
        return restituition;
    }

    public float getFriction() {
        return friction;
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
