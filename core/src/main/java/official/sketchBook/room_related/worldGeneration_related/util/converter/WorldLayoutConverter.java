package official.sketchBook.room_related.worldGeneration_related.util.converter;

import official.sketchBook.util_related.helpers.IO.RoomBlueprintIO;
import official.sketchBook.room_related.worldGeneration_related.generation.WorldLayout;
import official.sketchBook.room_related.worldGeneration_related.blueprint.RoomBlueprint;
import official.sketchBook.room_related.worldGeneration_related.blueprint.WorldLayoutBlueprint;

public class WorldLayoutConverter {

    private final RoomBlueprintIO blueprintIO;

    public WorldLayoutConverter(RoomBlueprintIO blueprintIO) {
        this.blueprintIO = blueprintIO;
    }

    //converte a partir de uma blueprint para um objeto
    public WorldLayout toObject(WorldLayoutBlueprint blueprint) {
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

        layout.setSourceBlueprint(blueprint);
        return layout;
    }

    //converte de um objeto para uma blueprint
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
