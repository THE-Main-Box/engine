package official.sketchBook.util_related.enumerators.layers;

public enum CollisionLayers {
    ENVIRONMENT     (1),
    PLAYER_ENTITY   (1 << 1),
    ENEMY_ENTITY    (1 << 2),
    PLAYER_PROJECTILE (1 << 3),
    ENEMY_PROJECTILE  (1 << 4),
    SENSOR          (1 << 5),
    INTERACTIVE     (1 << 6),
    NONE            (0);

    private final short bit;

    CollisionLayers(int bit) {
        this.bit = (short) bit;
    }

    public short bit() {
        return bit;
    }
}
