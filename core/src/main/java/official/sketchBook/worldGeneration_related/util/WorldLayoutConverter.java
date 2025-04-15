package official.sketchBook.worldGeneration_related.util;

import official.sketchBook.util_related.helpers.IO.RoomBlueprintIO;
import official.sketchBook.worldGeneration_related.generation.WorldLayout;
import official.sketchBook.worldGeneration_related.model.blueprint.RoomBlueprint;
import official.sketchBook.worldGeneration_related.model.blueprint.WorldLayoutBlueprint;

public class WorldLayoutConverter {

    private final RoomBlueprintIO blueprintIO;

    public WorldLayoutConverter(RoomBlueprintIO blueprintIO) {
        this.blueprintIO = blueprintIO;
    }

    public WorldLayout convert(WorldLayoutBlueprint blueprint) {
        int width = blueprint.getWidth();
        int height = blueprint.getHeight();
        WorldLayout layout = new WorldLayout(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String name = blueprint.getRoomName(x, y);
                if (name != null) {
                    RoomBlueprint bp = blueprintIO.load(name);
                    layout.setBlueprint(x, y, bp);
                }
            }
        }

        return layout;
    }

    public WorldLayoutBlueprint toBlueprint(WorldLayout layout) {
        int width = layout.getWidth();
        int height = layout.getHeight();
        WorldLayoutBlueprint blueprint = new WorldLayoutBlueprint(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RoomBlueprint bp = layout.getBlueprint(x, y);
                if (bp != null && bp.getTag() != null) {
                    blueprint.setRoomName(x, y, bp.getTag());
                }
            }
        }

        return blueprint;
    }
}
