package official.sketchBook.engine.util_related.utils.data_to_instance_related.world_gen_blueprint;

import official.sketchBook.game.util_related.enumerators.types.RoomType;

public class RoomBlueprint {
    private int[][] tileMap;
    private RoomType roomType;
    private String tag; // tipo "spawn", "passagem", "desvio", etc.

    public RoomBlueprint() {
    }

    public RoomBlueprint(int[][] tileMap, RoomType roomType, String tag) {
        this.tileMap = tileMap;
        this.roomType = roomType;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public int[][] getTileMap() {
        return tileMap;
    }

    public void setTileMap(int[][] tileMap) {
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
