package official.sketchBook.worldGeneration_related.structure;

import official.sketchBook.util_related.enumerators.types.TileType;

public class Tile {

    private TileType type;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }
}
