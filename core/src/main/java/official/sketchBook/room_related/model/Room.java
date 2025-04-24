package official.sketchBook.room_related.model;

import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;

public class Room {

    private final TileType[][] tiles;//tiles
    private final int widthInP, heightInP;//dimensões em pixels
    private final RoomType type;//tipo de sala
    private final String tag;//identificação

    public Room(TileType[][] tileTypes, RoomType roomType, String tag) {
        this.widthInP = tileTypes[0].length * PlayScreen.TILES_DEFAULT_SIZE;
        this.heightInP = tileTypes.length * PlayScreen.TILES_DEFAULT_SIZE;
        this.type = roomType;
        this.tag = tag;

        this.tiles = tileTypes;
    }

    //obtém a tile numa posição específica
    public TileType getTileAt(int x, int y) {
        if (y >= 0 && y < tiles.length && x >= 0 && x < tiles[y].length) {
            return tiles[y][x];
        }
        return null; // Fora dos limites
    }

    public String getTag() {
        return tag;
    }

    public RoomType getType() {
        return type;
    }

    public boolean canBeOverwritten() {
        return type == RoomType.NORMAL;
    }

    public TileType[][] getTiles() {
        return tiles;
    }

    public int getWidthInP() {
        return widthInP;
    }

    public int getHeightInP() {
        return heightInP;
    }

    public int getWidthInTiles() {
        return tiles[0].length;
    }

    public int getHeightInTiles() {
        return tiles.length;
    }
}
