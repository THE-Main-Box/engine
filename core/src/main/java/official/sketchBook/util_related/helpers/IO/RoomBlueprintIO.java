package official.sketchBook.util_related.helpers.IO;

import official.sketchBook.util_related.info.paths.LayoutAssetPath;
import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;

public class RoomBlueprintIO {

    private final JsonIO io = new JsonIO();

    public RoomBlueprint load(String name) {
        return io.load(LayoutAssetPath.roomBlueprint_path + name + ".json", RoomBlueprint.class, true);
    }

    public void save(RoomBlueprint blueprint) {
        io.save(LayoutAssetPath.roomBlueprint_path + blueprint.getTag() + ".json", blueprint, true);
    }

}
