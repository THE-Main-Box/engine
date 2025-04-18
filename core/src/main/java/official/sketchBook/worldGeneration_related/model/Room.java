package official.sketchBook.worldGeneration_related.model;

import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;

public class Room {

    private final TileType[][] tiles;
    private final int width, height;
    private final RoomType type;

    public Room(TileType[][] tileTypes, RoomType roomType) {
        this.width = tileTypes[0].length * PlayScreen.TILES_DEFAULT_SIZE;
        this.height = tileTypes.length * PlayScreen.TILES_DEFAULT_SIZE;
        this.type = roomType;

        this.tiles = tileTypes;
    }

    //obtém a tile numa posição específica
    public TileType getTileAt(int x, int y) {
        if (y >= 0 && y < tiles.length && x >= 0 && x < tiles[y].length) {
            return tiles[y][x];
        }
        return null; // Fora dos limites
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidthInTiles() {
        return tiles[0].length;
    }

    public int getHeightInTiles() {
        return tiles.length;
    }
}
