package official.sketchBook.worldGeneration_related.util;

import official.sketchBook.util_related.enumerators.types.TileType;
import official.sketchBook.worldGeneration_related.model.Room;
import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;

public class RoomConverter {

    public Room fromBlueprint(RoomBlueprint blueprint) {
        return new Room(
            convertToTileMap(blueprint.getTileMap()),
            blueprint.getRoomType()
        );
    }

    public RoomBlueprint fromObject(Room room, String tag) {
        return new RoomBlueprint(
            convertToIntegerTileMap(room.getTiles()),
            room.getType(),
            tag
        );
    }

    public TileType[][] convertToTileMap(int[][] raw) {
        TileType[][] result = new TileType[raw.length][raw[0].length];
        for (int y = 0; y < raw.length; y++) {
            for (int x = 0; x < raw[0].length; x++) {
                result[y][x] = TileType.fromId(raw[y][x]);
            }
        }
        return result;
    }

    public int[][] convertToIntegerTileMap(TileType[][] raw) {
        int[][] result = new int[raw.length][raw[0].length];
        for (int y = 0; y < raw.length; y++) {
            for (int x = 0; x < raw[0].length; x++) {
                result[y][x] = raw[y][x].getId();
            }
        }
        return result;
    }
}
