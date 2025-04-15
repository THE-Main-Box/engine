package official.sketchBook.worldGeneration_related.model;

import official.sketchBook.screen_related.PlayScreen;
import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;

public class Room {

    private final Tile[][] tiles;
    private final int width, height;
    private final RoomType type;

    public Room(TileType[][] tileTypes, RoomType roomType) {
        this.width = tileTypes[0].length * PlayScreen.TILES_DEFAULT_SIZE;
        this.height = tileTypes.length * PlayScreen.TILES_DEFAULT_SIZE;
        this.type = roomType;

        this.tiles = new Tile[tileTypes.length][tileTypes[0].length];
        initTiles(tileTypes);
    }

    //inicia a lista de tiles
    private void initTiles(TileType[][] tileTypes) {
        for (int y = 0; y < tileTypes.length; y++) {
            for (int x = 0; x < tileTypes[0].length; x++) {
                tiles[y][x] = new Tile(tileTypes[y][x]);
            }
        }
    }

    //valida se o tipo de tile é sólida
    public boolean isSolid(TileType type) {
        return switch (type) {
            case WALL, SLOPE_LEFT, SLOPE_RIGHT -> true;
            default -> false;
        };
    }

    //obtém a tile numa posição específica
    public Tile getTileAt(int x, int y) {
        if (y >= 0 && y < tiles.length && x >= 0 && x < tiles[y].length) {
            return tiles[y][x];
        }
        return null; // Fora dos limites
    }

    public boolean isSolid(int x, int y) {
        Tile tile = getTileAt(x, y);
        return tile != null && isTileSolid(tile.getType());
    }

    public RoomType getType() {
        return type;
    }

    public boolean canBeOverwritten() {
        return type == RoomType.NORMAL;
    }

    private boolean isTileSolid(TileType type) {
        return isSolid(type);
    }

    public Tile[][] getTiles() {
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
