package official.sketchBook.worldGeneration_related.structure;

import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;

public class RoomBlueprint {
    private final TileType[][] tileMap;
    private final RoomType roomType;
    private final String tag; // tipo "spawn", "passagem", "desvio", etc.

    public RoomBlueprint(TileType[][] tileMap, RoomType roomType, String tag) {
        this.tileMap = tileMap;
        this.roomType = roomType;
        this.tag = tag;
    }

    public Room create() {
        return new Room(tileMap, roomType);
    }

    public String getTag() {
        return tag;
    }
}
