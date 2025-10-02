package official.sketchBook.engine.room_related.worldGeneration_related.util.converter;

import official.sketchBook.game.util_related.enumerators.types.TileType;
import official.sketchBook.engine.room_related.model.Room;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.world_gen_blueprint.RoomBlueprint;

public class RoomConverter {

    public Room toObject(RoomBlueprint blueprint) {
        return new Room(
            convertToTileMap(blueprint.getTileMap()),
            blueprint.getRoomType(),
            blueprint.getTag()
        );
    }

    public RoomBlueprint toBlueprint(Room room) {
        return new RoomBlueprint(
            convertToIntegerTileMap(room.getTiles()),
            room.getType(),
            room.getTag()
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
