package official.sketchBook.engine.util_related.utils.IO;

import official.sketchBook.game.util_related.info.paths.LayoutAssetPath;
import official.sketchBook.engine.room_related.worldGeneration_related.blueprint.RoomBlueprint;

public class RoomBlueprintIO {

    private final JsonIO io = new JsonIO();

    public RoomBlueprint load(String name) {
        return io.load(LayoutAssetPath.roomBlueprint_path + name + ".json", RoomBlueprint.class, true);
    }

    public void save(RoomBlueprint blueprint) {
        io.save(LayoutAssetPath.roomBlueprint_path + blueprint.getTag() + ".json", blueprint, true);
    }

}
