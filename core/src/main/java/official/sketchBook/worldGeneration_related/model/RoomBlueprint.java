package official.sketchBook.worldGeneration_related.model;

import official.sketchBook.util_related.enumerators.types.RoomType;
import official.sketchBook.util_related.enumerators.types.TileType;

public class RoomBlueprint {
    private TileType[][] tileMap;
    private RoomType roomType;
    private String tag; // tipo "spawn", "passagem", "desvio", etc.

    public RoomBlueprint() {
    }

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

    public TileType[][] getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileType[][] tileMap) {
        this.tileMap = tileMap;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
